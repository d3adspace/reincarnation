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

package de.d3adspace.reincarnation.server.network;

import de.d3adspace.reincarnation.commons.action.ReincarnationNetworkAction;
import de.d3adspace.reincarnation.commons.netty.ReincarnationNettyChannelUtils;
import de.d3adspace.reincarnation.server.ReincarnationServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.IOException;
import java.net.SocketAddress;
import org.json.JSONObject;
import org.slf4j.Logger;

public class ReincarnationConnection extends SimpleChannelInboundHandler<JSONObject> {
	
	private static final Logger logger = ReincarnationServer.getLogger();
	private final ReincarnationServer reincarnationServer;
	private final Channel channel;
	private SocketAddress remoteSocketAddress;
	
	public ReincarnationConnection(
		ReincarnationServer reincarnationServer, Channel channel) {
		this.reincarnationServer = reincarnationServer;
		this.channel = channel;
		this.remoteSocketAddress = this.channel.remoteAddress();
	}
	
	public void sendObject(JSONObject jsonObject) {
		this.channel.writeAndFlush(jsonObject);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("Connection closed: {0}", this.remoteSocketAddress.toString());
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("Connection opened: {0}", this.remoteSocketAddress.toString());
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, JSONObject jsonObject)
		throws Exception {
		
		if (jsonObject == null || jsonObject.isNull("actionCode")) {
			return;
		}
		
		final ReincarnationNetworkAction action = ReincarnationNetworkAction.getViaId(
			(Integer) jsonObject.remove("actionCode"));
		
		switch (action) {
			case ACTION_BROADCAST: {
				final String channelName = jsonObject.getString("channel");
				this.reincarnationServer.getNetworkCommunicator()
					.broadcast(channelName, jsonObject);
				break;
			}
			case ACTION_REGISTER_CHANNEL: {
				final String channelName = jsonObject.getString("channel");
				this.reincarnationServer.getNetworkCommunicator().subscribe(channelName, this);
				break;
			}
			case ACTION_UNREGISTER_CHANNEL: {
				String channelName = jsonObject.getString("channel");
				this.reincarnationServer.getNetworkCommunicator().unsubscribe(channelName, this);
				break;
			}
			case ACTION_UNKNOWN: {
				logger
					.warn("received unknown action code by ", this.remoteSocketAddress.toString());
				break;
			}
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ReincarnationNettyChannelUtils.closeWhenFlushed(ctx.channel());
		
		if (!(cause instanceof IOException)) {
			cause.printStackTrace();
		}
	}
	
	public SocketAddress getRemoteSocketAddress() {
		return remoteSocketAddress;
	}
}
