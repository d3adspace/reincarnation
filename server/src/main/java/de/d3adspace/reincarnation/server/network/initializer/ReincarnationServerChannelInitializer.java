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

package de.d3adspace.reincarnation.server.network.initializer;

import de.d3adspace.reincarnation.commons.netty.ReincarnationNettyPipelineUtils;
import de.d3adspace.reincarnation.server.ReincarnationServer;
import de.d3adspace.reincarnation.server.network.ReincarnationConnection;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

public class ReincarnationServerChannelInitializer extends ChannelInitializer<Channel> {
	
	private final ReincarnationServer reincarnationServer;
	
	public ReincarnationServerChannelInitializer(ReincarnationServer reincarnationServer) {
		this.reincarnationServer = reincarnationServer;
	}
	
	protected void initChannel(Channel channel) throws Exception {
		final ChannelConfig channelConfig = channel.config();
		channelConfig.setAllocator(PooledByteBufAllocator.DEFAULT);
		
		final ChannelPipeline channelPipeline = channel.pipeline();
		channelPipeline.addLast(
			ReincarnationNettyPipelineUtils
				.createLengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4),
			ReincarnationNettyPipelineUtils.createJSONDecoder(),
			ReincarnationNettyPipelineUtils.createLengthFieldPrepender(4),
			ReincarnationNettyPipelineUtils.createJSONEncoder(),
			new ReincarnationConnection(this.reincarnationServer, channel)
		);
	}
}
