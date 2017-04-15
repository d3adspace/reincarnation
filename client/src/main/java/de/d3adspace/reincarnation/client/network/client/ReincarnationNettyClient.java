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

package de.d3adspace.reincarnation.client.network.client;

import de.d3adspace.reincarnation.client.network.initializer.ReincarnationClientChannelInitializer;
import de.d3adspace.reincarnation.commons.netty.ReincarnationNettyChannelUtils;
import de.d3adspace.reincarnation.commons.netty.ReincarnationNettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.IOException;
import org.json.JSONObject;

public abstract class ReincarnationNettyClient extends SimpleChannelInboundHandler<JSONObject> {
	
	private final String host;
	private final int port;
	private Channel channel;
	
	/**
	 * Start a new Netty Client - use with caution or use existing {@link
	 * de.d3adspace.reincarnation.client.network.publisher.ReincarnationPublisher} pr {@link
	 * de.d3adspace.reincarnation.client.network.subscriber.ReincarnationSubscriber}
	 *
	 * @param host host of the server
	 * @param port port of the server
	 */
	public ReincarnationNettyClient(String host, int port) {
		if (host.isEmpty()) {
			throw new IllegalArgumentException("host cannot be empty");
		}
		
		this.host = host;
		this.port = port;
		
		this.connect();
	}
	
	/**
	 * Called whenever the netty client received something in the right format of the pub sub impl
	 *
	 * @param jsonObject data received by the client
	 */
	protected abstract void received(JSONObject jsonObject);
	
	/**
	 * Called when the client connected successfully
	 */
	protected abstract void clientConnected();
	
	/**
	 * Connect to the server using host and port
	 */
	private void connect() {
		final EventLoopGroup workerGroup = ReincarnationNettyUtils.createEventLoopGroup(1);
		final Class<? extends Channel> channelClass = ReincarnationNettyUtils.getChannel();
		
		ChannelFuture channelFuture = new Bootstrap()
			.group(workerGroup)
			.channel(channelClass)
			.handler(new ReincarnationClientChannelInitializer(this))
			.option(ChannelOption.TCP_NODELAY, true)
			.connect(host, port);
		
		channelFuture.awaitUninterruptibly();
		this.channel = channelFuture.channel();
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, JSONObject jsonObject)
		throws Exception {
		
		this.received(jsonObject);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.clientConnected();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ReincarnationNettyChannelUtils.closeWhenFlushed(ctx.channel());
		
		if (!(cause instanceof IOException)) {
			cause.printStackTrace();
		}
	}
	
	/**
	 * write an jsonobject to the server and flush the channel
	 *
	 * @param jsonObject object to write
	 */
	protected void write(JSONObject jsonObject) {
		if (jsonObject == null) {
			throw new IllegalArgumentException("jsonObject cannot be null");
		}
		
		this.channel.writeAndFlush(jsonObject);
	}
	
	/**
	 * closes netty channel
	 */
	protected void closeConnection() {
		this.channel.close();
	}
}
