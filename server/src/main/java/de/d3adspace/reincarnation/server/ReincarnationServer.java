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

package de.d3adspace.reincarnation.server;

import de.d3adspace.reincarnation.commons.netty.ReincarnationNettyUtils;
import de.d3adspace.reincarnation.server.network.ReincarnationConnection;
import de.d3adspace.reincarnation.server.network.initializer.ReincarnationServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReincarnationServer {
	
	private static final Logger logger = LoggerFactory.getLogger(ReincarnationServer.class);
	private final String serverHost;
	private final int serverPort;
	
	private final Map<String, List<ReincarnationConnection>> channelSubscriptions;
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Channel serverChannel;
	
	public ReincarnationServer(String serverHost, int serverPort) {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.channelSubscriptions = new ConcurrentHashMap<>();
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	public void startServer() {
		if (ReincarnationNettyUtils.isEpoll()) {
			logger.info("Using epoll mechanism for netty.");
		}
		
		this.bossGroup = ReincarnationNettyUtils.createEventLoopGroup(1);
		this.workerGroup = ReincarnationNettyUtils.createEventLoopGroup(4);
		
		final Class<? extends ServerChannel> serverChannelClass = ReincarnationNettyUtils
			.getServerChannelClass();
		final ChannelHandler childHandler = new ReincarnationServerChannelInitializer(this);
		final InetSocketAddress inetSocketAddress = new InetSocketAddress(serverHost, serverPort);
		
		try {
			final ServerBootstrap serverBootstrap = new ServerBootstrap();
			this.serverChannel = serverBootstrap
				.group(bossGroup, workerGroup)
				.channel(serverChannelClass)
				.childHandler(childHandler)
				.option(ChannelOption.TCP_NODELAY, true)
				.bind(inetSocketAddress).sync().channel();
		} catch (InterruptedException e) {
			logger.error("Error occured while starting reincarnation netty server.", e);
		}
		
		logger.info("reincarnation server successfully started, listening on {0}:{1}", serverHost,
			serverPort);
	}
	
	public void stopServer() {
		logger.info("reincarnation server is going to stop.");
		
		this.serverChannel.close();
		
		this.bossGroup.shutdownGracefully();
		this.workerGroup.shutdownGracefully();
	}
	
	public void broadcast(String channel, final JSONObject jsonObject) {
		if (this.channelSubscriptions.containsKey(channel)) {
			this.channelSubscriptions.get(channel)
				.forEach(connection -> connection.sendObject(jsonObject));
		}
	}
	
	public void subscribe(String channelName, ReincarnationConnection connection) {
		if (this.channelSubscriptions.containsKey(channelName)) {
			this.channelSubscriptions
				.get(channelName)
				.add(connection);
		} else {
			this.channelSubscriptions.put(channelName,
				Collections.synchronizedList(Collections.singletonList(connection)));
		}
		
		logger.info("received new subscripton on {} by {}", channelName,
			connection.getRemoteSocketAddress().toString());
	}
	
	public void unsubscribe(String channelName, ReincarnationConnection connection) {
		if (this.channelSubscriptions.containsKey(channelName)) {
			this.channelSubscriptions.get(channelName).remove(connection);
			
			logger.info("received unsubscripton on {} by {}", channelName,
				connection.getRemoteSocketAddress().toString());
		}
	}
}
