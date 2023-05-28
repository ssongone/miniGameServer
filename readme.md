MMORPG 게임을 떠올려보자. 내가 게임에 접속하면 내가 움직이는 거 뿐만 아니라 다른 사람들의 움직임까지 화면에 띄워야 한다. 그 외에도 수많은 일들이 있겠지만 일단 위치만 감지해보겠다. 그리고 제목은 거창하게 게임 서버라고 했지만 사실은 비동기 처리를 공부하기 위한 토이 프로젝트라고 보면 되겄다. ㅎ_ㅎ

# 이름하야 위치 이동 감지 서버
### 결과물
![결과물.gif](https://velog.velcdn.com/images/song42782/post/c4bbe587-2a0b-4b1e-b5a1-6ba21c9a0be3/image.gif)

### 중점을 둔 부분

1. 자바 NIO를 이용해 **비동기 서버**를 구현. Selector를 이용한 이벤트 감지
2. **작업 스레트풀** : 메인스레드와 작업을 분리
3. **패킷**을 직접 만들고 객체 지향적으로 처리하기




### 게임(?) 설명

![맵.jpg](https://velog.velcdn.com/images/song42782/post/53f9ef9a-223e-4c88-9df6-4d704598b2dd/image.jpeg)


맵이 이렇게 36개로 나눠져있다. **내가 있는 곳**에서

1. 누가 움직이거나
2. 들어오거나 
3. 사라지면 

클라에게 메시지를 보내는 게 핵심 기능이다. 내가 있지 않은 곳에서 누가 움직이는 건 무시한다.

#### 상세 설명
클라이언트는 실제로 뭔가 움직이는 건 아니고 그냥 패킷을 보낸다. 여기서 클라는 패킷을 보낼 수 있다. 패킷 유형은 이름 설정 패킷과 이동 패킷 2가지다. 서버는 그 패킷을 열어보고 헤더에 따라 동작을 결정한다.
일단 클라가 연결되면 0,0에서 부터 시작한다!


## 1. NIO를 이용한 이벤트 기반 비동기 처리

그 전에 클라이언트 한명마다 스레드를 만들었다면 이제는 메인스레드가 이벤트를 감지해서 이벤트가 오는 쪽에 처리를 해줄 수 있도록 만들어 보았다.

이벤트는 NIO의 Selector를 이용해 감지할 수 있다. 스레드가 Selector를 가진다. Selector는 채널을 가진다. 여러 채널을 동시에 감시하고 있다. 이렇게 하면 단일스레드에서 여러 I/O 작업을 동시에 비동기적으로 할 수 있다.

![셀렉터.jpg](https://velog.velcdn.com/images/song42782/post/1e0c4eea-62f3-4287-8976-e0b73963e645/image.png)


또 그전엔 `인풋스트림`, `아웃풋스트림`을 이용했다면 이제는 채널을 이용해 인풋과 아웃풋을 동시에 처리한다.


#### 적용한 모습 

서버에 메인 스레드에 Selector를 만든다.
`selector = Selector.open();`

이 셀렉터에 여러가지 채널을 붙인다.
`serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);`
`clientChannel.register(selector, SelectionKey.OP_READ);
`
서버소켓채널은 수락이벤트를 감지한다. 연결이 되고 만들어진 소켓채널은 읽기 이벤트를 감지한다.

채널에 쓰는 부분은 따로 감지하는 부분을 두지 않고 바로 채널에 쓰도록 했다.

NIO에 대한 상세한 설명은 따로 별도의 포스팅에서.. (3일 안에..)

## 2. 메인 스레드와 작업을 분리

NioServer 클래스는 앞에서 말했듯이 소켓의 연결과 데이터 수신을 감지하고 있다. 지금이야 소규모라 빠릿하게 처리가 되지만 나중에 점점 규모가 커지면 요청도 많이 올것이고 데이터를 처리하는 부분도 오래걸릴 것이다. 특히 클라이언트에게 전체 메시지를 보낼때.. 규모가 커지면 오래걸릴 것 같았다. 

그래서 서버클래스에서 패킷이 수신됐을 때 처리하는 부분은 스레드풀로 넘겨주었다.

#### 람다와 함수형 프로그래밍의 맛

``` java
executorService.execute(() -> packetHandler.processBuff(user, buffer));

```



## 3. 패킷

#### 패킷 구조

![패킷.jpg](https://velog.velcdn.com/images/song42782/post/90bd6d1f-a854-42a2-aebd-5d4f3c1afa95/image.jpeg)

패킷크기는 최대 64바이트. 앞에 2바이트가 헤더고 나머지가 바디다. (바디 사이즈가 없으면 뒤에 빈칸 처리가 힘들었다...)

클라이언트는 
"1 이름아무거나"
"2 100,100"
(유형 내용)
이런식으로 입력해서 패킷을 보낼 수 있다. 


#### 서버에서 처리하기

서버에는 `PacketHandler` 클래스가 있다. 거기에 유형별 패킷을 매칭해둔다.

``` java
    Map<Integer, Packet> map = new HashMap<>();

```
첫번째 부분을 읽고 map에서 유형에 맞는 Packet을 가져온다.

``` java
public void processBuff(User user, ByteBuffer buffer) {

        buffer.flip();
        int type = buffer.get();
        Packet packet = map.get(type);

        int size = buffer.get();
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        String input = new String(bytes, StandardCharsets.UTF_8);

        packet.readBody(user, input);
    }
```

구현체마다 오버라이드한 `readBody()`를 이용해 처리한다.
매개변수를 받아서 처리하는 Packet의 역할을 명확하게 하기 위해 BiConsumer를 상속 받아보았다. 



![패킷처리.jpg](https://velog.velcdn.com/images/song42782/post/a471f046-f9c9-4b94-8335-468fd1f41fef/image.png)



이렇게 만들었기 때문에 추후에 내가 기능을 추가하더라도 (아이템 획득, 채팅 등등) 패킷을 구현한 클래스를 만들고 맵에 추가하기만 하면 다른 부분을 수정하지 않아도 잘 동작할 것이다. 

## 마무리
위에까진 서버 부분 상세 설명이었고 클라이언트는 버퍼 만들고 메시지 보내는 식으로 만들었다. 서버로부터 오는 메시지 수신은 별도의 스레드로 빼서 병렬적으로 동작하도록 만들었다.

C++에 IOCP가 있다면 자바엔 그보다 못하지만 NIO가 있다고 해서 한번 써봤는데 IO와 넌블러킹, 그리고 동시성 처리에 대해 좀 더 깊게 알게된 것 같다.







