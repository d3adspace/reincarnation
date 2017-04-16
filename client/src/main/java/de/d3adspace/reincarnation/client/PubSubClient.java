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

package de.d3adspace.reincarnation.client;

import de.d3adspace.reincarnation.client.network.handler.ReincarnationSubscriptionHandler;
import org.json.JSONObject;

public interface PubSubClient {
	
	/**
	 * You can publish the given jsonObject to the given channel.
	 *
	 * @param channelName The name of the channel to publish to.
	 * @param jsonObject The JSONObject to publish.
	 */
	void publish(String channelName, JSONObject jsonObject);
	
	/**
	 * Subscribe to the given channel by implementing the {@link ReincarnationSubscriptionHandler} and giving it a channel
	 * using {@link de.d3adspace.reincarnation.commons.annotation.SubscriptionChannel}.
	 *
	 * @param subscriptionHandler The Handler to handle the received objects.
	 */
	void subscribe(ReincarnationSubscriptionHandler subscriptionHandler);
	
	/**
	 * Unsubscribe the given channel by removing all handlers.
	 *
	 * @param channelName The name of the channel to unsubscribe.
	 */
	void unsubscribe(String channelName);
	
	/**
	 * Unsubscribe a single handler, not the whole channel.
	 *
	 * @param handler The handler to unregister.
	 */
	void unsubscribe(ReincarnationSubscriptionHandler handler);
	
	/**
	 * Ask whether you have subscribed a given channel.
	 *
	 * @param channelName The name of the channel.
	 * @return Whether there is a subscription for that channel.
	 */
	boolean hasSubscribed(String channelName);
	
	/**
	 * Disconnect from the server. Will remove all subscriptions and shutdown the client.
	 */
	void disconnect();
}
