# Reincarnation

Usage: 
````
java -jar reincarnation-server-1.2.0-SNAPSHOT.jar -h <host> -p <port>
````

```java
class App {
		
		public static void main(String[] args) {
			final ReincarnationPubSubClient pubSubClient = new ReincarnationPubSubClient(
				"localhost", 10000);
			
			pubSubClient.subscribe(new CustomHandler());
			pubSubClient.subscribe(new CustomHandler1());
			
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			pubSubClient.publish("example2", new JSONObject().put("text", "HEY!"));
		}
		
		@Channel(channelName = "example")
		private static class CustomHandler implements SubscriptionHandler {
			
			@Override
			public void receivedMessage(JSONObject jsonObject) {
				System.out.println("Publish in example" + jsonObject.toString());
			}
		}
		
		@Channel(channelName = "idioten2")
		private static class CustomHandler1 implements SubscriptionHandler {
			
			@Override
			public void receivedMessage(JSONObject jsonObject) {
				System.out.println("Publish in example2" + jsonObject.toString());
			}
		}
	}
```