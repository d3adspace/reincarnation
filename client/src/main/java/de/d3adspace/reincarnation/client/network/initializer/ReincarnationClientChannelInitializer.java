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

package de.d3adspace.reincarnation.client.network.initializer;

import de.d3adspace.reincarnation.client.network.client.ReincarnationNettyClient;
import de.d3adspace.reincarnation.commons.netty.ReincarnationNettyPipelineUtils;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class ReincarnationClientChannelInitializer extends ChannelInitializer<Channel> {
	
	private final ReincarnationNettyClient reincarnationClient;
	
	public ReincarnationClientChannelInitializer(ReincarnationNettyClient reincarnationClient) {
		this.reincarnationClient = reincarnationClient;
	}
	
	@Override
	protected void initChannel(Channel channel) throws Exception {
		channel.config().setAllocator(PooledByteBufAllocator.DEFAULT);
		
		channel.pipeline().addLast(
			ReincarnationNettyPipelineUtils
				.createLengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4),
			ReincarnationNettyPipelineUtils.createJSONDecoder(),
			ReincarnationNettyPipelineUtils.createLengthFieldPrepender(4),
			ReincarnationNettyPipelineUtils.createJSONEncoder(),
			reincarnationClient
		);
	}
}
