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

package de.d3adspace.reincarnation.client.network;

import de.d3adspace.reincarnation.client.network.publisher.ReincarnationPublisher;
import de.d3adspace.reincarnation.client.network.subscriber.ReincarnationSubscriber;
import de.d3adspace.reincarnation.client.network.subscriber.handler.SubscriptionHandler;
import java.util.function.Consumer;
import org.json.JSONObject;

public class ReincarnationPubSubClient {
	
	private final ReincarnationSubscriber subscriber;
	private final ReincarnationPublisher publisher;
	
	public ReincarnationPubSubClient(String host, int port) {
		this.subscriber = new ReincarnationSubscriber(host, port);
		this.publisher = new ReincarnationPublisher(host, port);
	}
	
	public void publish(String channelName, JSONObject jsonObject) {
		this.publisher.publish(channelName, jsonObject);
	}
	
	public void subscribe(SubscriptionHandler subscriptionHandler) {
		this.subscriber.subscribe(subscriptionHandler);
	}
	
	public void request(String channelName, JSONObject request, Consumer<JSONObject> consumer) {
		this.publisher.request(channelName, request, consumer);
	}
	
	public void disconnect() {
		this.publisher.disconnect();
		this.subscriber.disconnect();
	}
}
