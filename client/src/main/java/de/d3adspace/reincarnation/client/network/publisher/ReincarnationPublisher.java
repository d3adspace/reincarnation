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

package de.d3adspace.reincarnation.client.network.publisher;

import de.d3adspace.reincarnation.client.network.client.ReincarnationNettyClient;
import de.d3adspace.reincarnation.commons.action.ReincarnationNetworkAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.json.JSONObject;

public class ReincarnationPublisher extends ReincarnationNettyClient {
	
	private final AtomicInteger CALLBACK_ID = new AtomicInteger(0);
	private final Map<Integer, Consumer<JSONObject>> callbacks;
	
	private final ExecutorService executorService = Executors.newSingleThreadExecutor(runnable -> {
		final Thread thread = Executors.defaultThreadFactory().newThread(runnable);
		thread.setName("PublisherThread");
		return thread;
	});
	
	public ReincarnationPublisher(String host, int port) {
		super(host, port);
		
		this.callbacks = new ConcurrentHashMap<>();
	}
	
	@Override
	public void received(JSONObject jsonObject) {
		if (this.callbacks.isEmpty()) {
			return;
		}
		if (!jsonObject.has("callbackId")) {
			return;
		}
		
		final int callbackId = Integer
			.valueOf(((String) jsonObject.remove("callbackId")).split("|")[1]);
		if (this.callbacks.containsKey(callbackId)) {
			final Consumer<JSONObject> consumer = this.callbacks.get(callbackId);
			consumer.accept(jsonObject);
		}
	}
	
	@Override
	protected void clientConnected() {
		// We dont do that much
	}
	
	public void disconnect() {
		this.executorService.shutdown();
		super.closeConnection();
	}
	
	public void publish(String channelName, JSONObject jsonObject) {
		this.publish(channelName, null, jsonObject);
	}
	
	public void publish(String channelName, String subscriberName, JSONObject jsonObject) {
		if (channelName.isEmpty()) {
			throw new IllegalArgumentException("channel cannot have an empty name");
		}
		if (jsonObject == null) {
			throw new IllegalArgumentException("jsonObject cannot be null");
		}
		
		jsonObject.put("actionCode", ReincarnationNetworkAction.ACTION_BROADCAST.getActionCode());
		jsonObject.put("channel", channelName);
		
		if (subscriberName != null) {
			jsonObject.put("subscriberName", subscriberName);
		}
		
		this.executorService.execute(() -> super.write(jsonObject));
	}
	
	public void request(String channelName, JSONObject request, Consumer<JSONObject> consumer) {
		final String callbackId = this.getName() + ";" + this.CALLBACK_ID.getAndIncrement();
		
		request.put("actionCode", ReincarnationNetworkAction.ACTION_UNKNOWN.getActionCode());
	}
}
