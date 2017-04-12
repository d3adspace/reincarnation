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

package de.d3adspace.reincarnation.commons.netty;

import de.d3adspace.reincarnation.commons.netty.codec.ReincarnationJSONDecoder;
import de.d3adspace.reincarnation.commons.netty.codec.ReincarnationJSONEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class ReincarnationNettyPipelineUtils {
	
	public static ChannelHandler createLengthFieldBasedFrameDecoder(int maxFrameLength, int offset,
		int lengthFieldLength) {
		return new LengthFieldBasedFrameDecoder(maxFrameLength, offset, lengthFieldLength);
	}
	
	public static ChannelHandler createLengthFieldPrepender(int lengthFieldLength) {
		return new LengthFieldPrepender(lengthFieldLength);
	}
	
	public static ChannelHandler createJSONDecoder() {
		return new ReincarnationJSONDecoder();
	}
	
	public static ChannelHandler createJSONEncoder() {
		return new ReincarnationJSONEncoder();
	}
}
