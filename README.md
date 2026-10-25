# 토비의 스프링 3.1 · 1장 코드 실습

## 1.6 싱글톤 레지스트리와 오브젝트 스코프

기존 1.4 코드의 `DaoFactory`와 `UserDao`는 그대로 사용한다. `DaoFactory.userDao()`를 직접 두 번 호출하면 서로 다른 객체가 나온다. `SpringDaoConfig`를 컨테이너에 등록하고 같은 `userDao` 빈을 두 번 조회하면 같은 객체가 나온다. 이 싱글톤의 범위는 **빈 정의 하나와 스프링 컨테이너 하나**이다.

Java 17 이상과 Maven이 필요하다. 프로젝트 루트에서 실행한다.

```bash
mvn test
mvn -q compile exec:java -Dexec.mainClass=study.toby.scope.ScopeDemo
```

`ScopeDemo` 출력의 참/거짓은 순서대로 `false`, `true`, `false`, `false`이다. 웹 스코프와 동시 요청 상태 실험은 자동 테스트에서 실행한다.

| 확인할 것 | 코드와 테스트 |
| --- | --- |
| 일반 팩토리와 스프링 싱글톤 | `SpringDaoConfig`, `SingletonRegistryTest` |
| `==`와 `equals()` | `SingletonRegistryTest` |
| 요청 중 바뀌는 필드의 위험 | `UnsafeRequestState`, `StateSafetyTest` |
| 안전한 메서드 인자와 지역값 | `StatelessUserService`, 기존 `UserDao` |
| prototype 직접 주입과 매번 조회 | `ScopeConfig`, `FixedTokenClient`, `FreshTokenClient`, `ScopeTest` |
| request/session 범위 | `ScopeConfig`, `ScopeTest`의 모의 HTTP 요청/세션 |

`UnsafeRequestState`는 잘못된 공유 상태를 보여주기 위한 예제다. 실제 DAO에서는 요청마다 달라지는 ID나 `Connection`을 공유 인스턴스 필드에 저장하지 않는다. 기존 `UserDao`의 `ConnectionMaker`는 생성자에서 주입받는 `final` 필드이고, JDBC `Connection`과 SQL 작업 변수는 메서드 안에서 만든다. `request`와 `session` 빈은 웹 인식 컨텍스트와 실제 요청 문맥이 있을 때만 사용할 수 있다. 싱글톤에 prototype 빈을 직접 주입하면 주입 시점의 객체 하나를 계속 쓰므로, 매번 새 객체가 필요하면 `ObjectProvider`로 조회한다.

책은 Spring 3.1을 설명하지만 이 실행 예제는 Java 17과 Spring Framework 6.1.13을 사용한다. 개념을 현재 API로 확인하기 위한 코드이며 책의 원본 예제를 그대로 옮긴 것은 아니다.

- [1.6 개인 원문](https://app.notion.com/p/404b48e0c8ae8035ae05f0ef00757f0c)
- [1.6 학습 정리](https://app.notion.com/p/404b48e0c8ae81c6a2b1e61bd085bbb1)
- [Spring 공식 문서: 빈 스코프](https://docs.spring.io/spring-framework/reference/6.2/core/beans/factory-scopes.html)

## 1.4 DaoFactory 실습

**가설:** `UserDao`가 `ConnectionMaker` 인터페이스를 생성자로 받으면, 연결 구현체 선택을 `DaoFactory`에 맡길 수 있고 DAO 코드 수정 없이 교체할 수 있다.

## 실행

- Java 17 이상, Maven 3.9 이상
- `mvn test`: 자동 테스트
- `mvn compile exec:java`: 두 구현체로 각각 저장하고 조회하는 데모

첫 실행 시 Maven Central에서 H2와 JUnit을 내려받는다. 별도 DB 서버는 필요 없다. H2 메모리 DB 두 개를 사용한다.

## 살펴볼 코드

1. `ConnectionMaker`: DB 연결을 만드는 추상화.
2. `UserDao`: 생성자로 받은 연결 제공자만 사용하며 SQL 처리에 집중한다.
3. `DaoFactory`: `ConnectionMaker`와 `UserDao`를 연결한다.
4. `PrimaryConnectionMaker`와 `AlternateConnectionMaker`: 서로 다른 H2 DB를 제공한다.
5. `DaoFactoryTest`: 같은 `UserDao` 코드가 두 연결 구현체에서 작동하고 데이터가 분리됨을 확인한다.

실제 DB 제품을 바꾸는 실습은 아니다. 여기서는 연결 정책과 DB 인스턴스의 교체를 검증한다. H2는 DB 설치 없이 JDBC 동작을 확인하기 위한 실습용이다. 비밀번호는 책의 예제 구조를 따라 저장하지만, 운영 환경의 비밀번호 저장 방식으로 사용하면 안 된다.

## 원문

- [1장 오브젝트와 의존관계](https://app.notion.com/p/3eeb48e0c8ae8095ae72db22543e453b)
- [1장 오브젝트와 의존관계 (2): 1.4 DaoFactory](https://app.notion.com/p/3f6b48e0c8ae800993e7d74286468980)
- [개인 책 학습 워크플로우 분류안](https://app.notion.com/p/404b48e0c8ae81e2a342dcf4eaf76eed)

## 실습 요약 Notion 링크

[1.4 DaoFactory — 생성자 주입과 객체 조립](https://app.notion.com/p/404b48e0c8ae81e98953cc057f03fe5e)
