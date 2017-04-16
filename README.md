# Reincarnation

Usage: 
````bash
java -jar reincarnation-server-1.2.0-SNAPSHOT.jar -h <host> -p <port>
````

```java
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


import de.d3adspace.reincarnation.client.PubSubClient;
import de.d3adspace.reincarnation.client.network.ReincarnationPubSubClientFactory;
import de.d3adspace.reincarnation.client.network.handler.ReincarnationSubscriptionHandler;
import de.d3adspace.reincarnation.commons.annotation.SubscriptionChannel;
import org.json.JSONObject;

public class Main {
	public static void main(String[] args) {
		final PubSubClient pubSubClient = ReincarnationPubSubClientFactory.createPubSubClient("localhost", 10000);
		
		pubSubClient.subscribe(new CustomHandler());
		
		pubSubClient.publish("example", new JSONObject().put("text", "test!"));
		
		pubSubClient.unsubscribe("example");
		
		pubSubClient.disconnect();
	}
	
	@SubscriptionChannel(channelName = "example")
	private static class CustomHandler implements ReincarnationSubscriptionHandler {
		
		@Override
		public void onMessage(JSONObject jsonObject) {
			System.out.println("Publish in example: " + jsonObject.toString());
		}
	}
}
```