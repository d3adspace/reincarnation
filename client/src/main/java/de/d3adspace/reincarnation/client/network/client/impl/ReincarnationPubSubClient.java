/*
 * Copyright (c) 2017 D3adspace
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package de.d3adspace.reincarnation.client.network.client.impl;

import de.d3adspace.reincarnation.client.network.PubSubClient;
import de.d3adspace.reincarnation.client.network.client.handler.ReincarnationSubscriptionHandler;
import de.d3adspace.reincarnation.client.network.client.pipe.ReincarnationPubSubClientChannelHandler;
import de.d3adspace.reincarnation.client.network.initializer.ReincarnationClientChannelInitializer;
import de.d3adspace.reincarnation.commons.action.ReincarnationNetworkAction;
import de.d3adspace.reincarnation.commons.annotation.SubscriptionChannel;
import de.d3adspace.reincarnation.commons.netty.ReincarnationNettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

public class ReincarnationPubSubClient implements PubSubClient {
	
	private final ExecutorService executorService;
	private final String host;
	private final int port;
	private final Map<String, List<ReincarnationSubscriptionHandler>> handlers;
	private Channel channel;
	
	public ReincarnationPubSubClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.executorService = Executors.newSingleThreadExecutor(runnable -> {
			final Thread thread = Executors.defaultThreadFactory().newThread(runnable);
			thread.setName("PublisherThread #1");
			return thread;
		});
		this.handlers = new ConcurrentHashMap<>();
		
		this.connectToServer();
	}
	
	private void connectToServer() {
		final EventLoopGroup workerGroup = ReincarnationNettyUtils.createEventLoopGroup(1);
		final Class<? extends Channel> channelClass = ReincarnationNettyUtils.getChannel();
		
		ChannelFuture channelFuture = new Bootstrap()
			.group(workerGroup)
			.channel(channelClass)
			.handler(new ReincarnationClientChannelInitializer(
				new ReincarnationPubSubClientChannelHandler(this)))
			.option(ChannelOption.TCP_NODELAY, true)
			.connect(host, port);
		
		channelFuture.awaitUninterruptibly();
		this.channel = channelFuture.channel();
	}
	
	@Override
	public void publish(String channelName, JSONObject jsonObject) {
		if (channelName.isEmpty()) {
			throw new IllegalArgumentException("channel cannot have an empty name");
		}
		if (jsonObject == null) {
			throw new IllegalArgumentException("jsonObject cannot be null");
		}
		
		jsonObject.put("actionCode", ReincarnationNetworkAction.ACTION_BROADCAST.getActionCode());
		jsonObject.put("channel", channelName);
		
		this.write(jsonObject);
	}
	
	@Override
	public void subscribe(ReincarnationSubscriptionHandler subscriptionHandler) {
		final String channelName = this.getChannelNameFromHandler(subscriptionHandler);
		this.registerHandler(channelName, subscriptionHandler);
		
		final JSONObject jsonObject = new JSONObject()
			.put("actionCode", ReincarnationNetworkAction.ACTION_REGISTER_CHANNEL.getActionCode())
			.put("channel", channelName);
		
		this.write(jsonObject);
	}
	
	@Override
	public boolean hasSubscribed(String channelName) {
		return this.handlers.containsKey(channelName);
	}
	
	public void received(JSONObject jsonObject) {
		if (jsonObject.length() < 1) {
			return;
		}
		
		final String channelName = (String) jsonObject.remove("channel");
		if (this.handlers.containsKey(channelName)) {
			this.handlers.get(channelName).forEach(handler -> handler.onMessage(jsonObject));
		}
	}
	
	private String getChannelNameFromHandler(ReincarnationSubscriptionHandler subscriptionHandler) {
		return subscriptionHandler.getClass().getAnnotation(SubscriptionChannel.class)
			.channelName();
	}
	
	private void registerHandler(String channelName,
		ReincarnationSubscriptionHandler subscriptionHandler) {
		if (this.handlers.containsKey(channelName)) {
			this.handlers.get(channelName).add(subscriptionHandler);
		} else {
			this.handlers.put(channelName, Collections.singletonList(subscriptionHandler));
		}
	}
	
	private void write(JSONObject jsonObject) {
		this.executorService.execute(() -> this.channel.writeAndFlush(jsonObject));
	}
}
