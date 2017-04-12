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

package de.d3adspace.reincarnation.client.client;

import de.d3adspace.reincarnation.client.network.initializer.ReincarnationClientChannelInitializer;
import de.d3adspace.reincarnation.commons.netty.ReincarnationNettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.JSONObject;

public abstract class ReincarnationClient extends SimpleChannelInboundHandler<JSONObject> {
	
	private final String host;
	private final int port;
	private final String name;
	private Channel channel;
	
	public ReincarnationClient(String host, int port) {
		this(host, port, "server");
	}
	
	public ReincarnationClient(String host, int port, String name) {
		this.host = host;
		this.port = port;
		this.name = name;
		
		this.connect();
	}
	
	public abstract void received(JSONObject jsonObject);
	
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
	
	protected void write(JSONObject jsonObject) {
		this.channel.writeAndFlush(jsonObject);
	}
	
	protected void closeConnection() {
		this.channel.close();
	}
}
