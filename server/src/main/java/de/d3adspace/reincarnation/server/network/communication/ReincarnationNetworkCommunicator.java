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

package de.d3adspace.reincarnation.server.network.communication;

import de.d3adspace.reincarnation.server.network.ReincarnationConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.slf4j.Logger;

public class ReincarnationNetworkCommunicator {
	
	private final Logger logger;
	private final Map<String, List<ReincarnationConnection>> sessions;
	
	public ReincarnationNetworkCommunicator(Logger logger) {
		this.logger = logger;
		this.sessions = new ConcurrentHashMap<>();
	}
	
	public void broadcast(String channel, final JSONObject jsonObject) {
		if (this.sessions.containsKey(channel)) {
			this.sessions.get(channel)
				.forEach(connection -> connection.sendObject(jsonObject));
		}
	}
	
	public void broadcastToSubscriber(String channelName, String subscriberName,
		JSONObject jsonObject) {
		if (this.sessions.containsKey(channelName)) {
			for (ReincarnationConnection connection : this.sessions.get(channelName).stream()
				.filter(connection -> Objects
					.equals(connection.getSubscriberName(), subscriberName))
				.collect(Collectors.toList())) {
				connection.sendObject(jsonObject);
			}
		}
	}
	
	public void subscribe(String channelName, ReincarnationConnection connection) {
		if (this.sessions.containsKey(channelName)) {
			this.sessions
				.get(channelName)
				.add(connection);
		} else {
			this.sessions.put(channelName,
				new CopyOnWriteArrayList<>(Collections.singletonList(connection)));
		}
		
		logger.info("received new subscripton on {} by {}", channelName,
			connection.getRemoteSocketAddress().toString());
	}
	
	public void unsubscribe(String channelName, ReincarnationConnection connection) {
		if (this.sessions.containsKey(channelName)) {
			this.sessions.get(channelName).remove(connection);
			
			logger.info("received unsubscripton on {} by {}", channelName,
				connection.getRemoteSocketAddress().toString());
		}
	}
}
