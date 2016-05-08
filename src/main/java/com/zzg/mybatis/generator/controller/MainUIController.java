package com.zzg.mybatis.generator.controller;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class MainUIController implements Initializable {

	@FXML
	private TextField nodesField;

	@FXML
	private TextField masterField;

	@FXML
	private TextField databaseField;

	@FXML
	private Label connectStatusLabel;

	@FXML
	private Label typeLabel;

	@FXML
	private Button connectBtn;

	@FXML
	private TreeView<String> treeView;

	@FXML
	private ListView<String> valueListView;

	private StringRedisTemplate redis;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	void connectToRedis() {
		String redisNodes = nodesField.getText();
		String master = masterField.getText();
		redis = new StringRedisTemplate();
		redis.setKeySerializer(new StringRedisSerializer());
		RedisSentinelConfiguration config = new RedisSentinelConfiguration();
		config.setMaster(master);
		String[] nodes = redisNodes.split(",");
		for (String node : nodes) {
			String[] hostport = node.split(":");
			String host = hostport[0];
			int port = Integer.parseInt(hostport[1]);
			config.addSentinel(new RedisNode(host, port));
		}
		JedisConnectionFactory factory = new JedisConnectionFactory(config);
		factory.setUsePool(true);
		factory.afterPropertiesSet();
		redis.setConnectionFactory(factory);
		redis.afterPropertiesSet();

		connectStatusLabel.setText("连接成功");
		testKeys();
	}

	@FXML
	void testKeys() {
		TreeItem<String> rootItem = new TreeItem<>("Root");
		treeView.setRoot(rootItem);
		treeView.setShowRoot(false);
		Set<String> keys = redis.keys("Cobra_*");
		if (keys != null && keys.size() > 0) {
			keys.stream().forEach(key -> {
				TreeItem<String> item = new TreeItem<>();
				Label label = new Label(key);
				// label.setBackground(new Background(new BackgroundFill(new Color(1, 0, 0, 1), null, null)));
				label.setOnMouseClicked(event -> {
					ObservableList<String> observableList = FXCollections.observableArrayList();
					DataType type = redis.type(key);
					String code = type.code();
					typeLabel.setText(code);
					System.out.println(code);
					Collection<?> values = null;
					if ("zset".equals(code)) {
						Set<TypedTuple<String>> hashKeys = redis.opsForZSet().rangeWithScores(key, 0, -1);
						if (hashKeys != null && hashKeys.size() > 0) {
							values = hashKeys.stream().map(
									tuple -> "value: " + tuple.getValue() + ", score: " + tuple.getScore().longValue())
									.collect(Collectors.toList());
						}
						System.out.println(values);
					} else if ("set".equals(code)) {
						values = redis.opsForSet().members(key);
						System.out.println(key + ": " + values);
					} else if ("hash".equals(code)) {
						Set<Object> hashKeys = redis.opsForHash().keys(key);
						if (hashKeys != null && hashKeys.size() > 0) {
							values = hashKeys.stream().map(
									hashKey -> "Key: " + hashKey + ", Value: " + redis.opsForHash().get(key, hashKey))
									.collect(Collectors.toList());
						}
						System.out.println(key + ": " + values);
					} else if ("list".equals(code)) {
						values = redis.opsForList().range(key, 0, -1);
						System.out.println(values);
					}

					if (values != null && values.size() > 0) {
						values.stream().forEach(value -> {
							observableList.add(value.toString());
						});
					}
					valueListView.setItems(observableList);
				});
				item.setGraphic(label);
				item.setValue("");
				rootItem.getChildren().add(item);
			});
		}
	}

	@FXML
	void clearAllKeys() {
		Set<String> keys = redis.keys("Cobra_*");
		if (keys != null && keys.size() > 0) {
			keys.stream().forEach(key -> {
				redis.delete(key);
			});
		}
	}

}
