# Reincarnation

Usage: 
````bash
java -jar reincarnation-server-1.2.0-SNAPSHOT.jar -h <host> -p <port>
````

```java
public class Main {
	public static void main(String[] args) {
		final PubSubClient pubSubClient = ReincarnationPubSubClientFactory.createPubSubClient("localhost", 10000);
		
		pubSubClient.subscribe(new CustomHandler());
		
		pubSubClient.publish("example", new JSONObject().put("text", "test!"));
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