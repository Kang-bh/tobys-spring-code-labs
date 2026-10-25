# 1장 오브젝트와 의존관계

이 장의 실습은 1.4의 `DaoFactory`와 `UserDao`를 1.6에서도 사용한다. Java 17 이상과 Maven 3.9 이상이 필요하며, H2 메모리 DB를 사용하므로 별도 DB 서버는 필요 없다.

| 절 | 핵심 | 설명 및 코드 |
| --- | --- | --- |
| 1.4 DaoFactory | 생성자 주입과 객체 조립 | [학습 정리](https://app.notion.com/p/404b48e0c8ae81e98953cc057f03fe5e) · [dao 코드](src/main/java/study/toby/chapter01/) · [테스트](src/test/java/study/toby/chapter01/DaoFactoryTest.java) |
| 1.6 싱글톤 레지스트리와 오브젝트 스코프 | 컨테이너의 객체 재사용, 공유 상태, 스코프 | [학습 정리](https://app.notion.com/p/404b48e0c8ae81c6a2b1e61bd085bbb1) · [scope 코드](src/main/java/study/toby/chapter01/scope/) · [테스트](src/test/java/study/toby/chapter01/scope/) |

## 1.4 DaoFactory

`UserDao`는 `ConnectionMaker` 인터페이스를 생성자로 받는다. `DaoFactory`가 연결 구현체를 선택해 전달하므로, 연결 제공자를 바꿀 때 DAO 코드를 수정하지 않는다. 두 구현체는 서로 다른 H2 메모리 DB를 제공한다.

```bash
mvn -q compile exec:java -Dexec.mainClass=study.toby.chapter01.Demo
```

`DaoFactoryTest`는 같은 `UserDao` 코드가 두 연결 구현체에서 작동하고 데이터가 분리됨을 확인한다. 실제 DB 제품 교체를 검증하는 실습은 아니다.

## 1.6 싱글톤 레지스트리와 오브젝트 스코프

`DaoFactory.userDao()`를 직접 두 번 호출하면 서로 다른 DAO가 만들어진다. `SpringDaoConfig`를 등록한 같은 스프링 컨테이너에서 `userDao` 빈을 두 번 조회하면 같은 객체가 반환된다. 싱글톤의 범위는 **빈 정의 하나와 컨테이너 하나**다.

```bash
mvn -q compile exec:java -Dexec.mainClass=study.toby.chapter01.scope.ScopeDemo
mvn test
```

`ScopeDemo`의 참/거짓 출력은 순서대로 `false`, `true`, `false`, `false`다.

| 확인할 것 | 코드와 테스트 |
| --- | --- |
| 일반 팩토리와 스프링 싱글톤, `==`와 `equals()` | `SpringDaoConfig`, `SingletonRegistryTest` |
| 공유 필드에 요청 상태를 저장할 때의 위험 | `UnsafeRequestState`, `StateSafetyTest` |
| 요청별 값을 메서드 인자로 다루기 | `StatelessUserService`, `UserDao` |
| prototype의 직접 주입과 매번 조회 | `ScopeConfig`, `FixedTokenClient`, `FreshTokenClient`, `ScopeTest` |
| request/session 범위 | `ScopeConfig`, `ScopeTest`의 모의 HTTP 요청/세션 |

`UnsafeRequestState`는 잘못된 공유 상태를 보여 주는 예제다. `UserDao`의 `ConnectionMaker`는 생성자에서 주입받는 `final` 필드이고, JDBC `Connection`과 SQL 작업 변수는 메서드 안에서 만든다. request와 session 빈은 웹 인식 컨테이너 및 요청 문맥에서 사용할 수 있다. 싱글톤에 prototype 빈을 직접 주입하면 주입 시점의 객체 하나를 계속 쓰므로, 매번 새 객체가 필요하면 `ObjectProvider`로 조회한다.

## 참고

- [1장 개인 원문](https://app.notion.com/p/3eeb48e0c8ae8095ae72db22543e453b)
- [1.6 개인 원문](https://app.notion.com/p/404b48e0c8ae8035ae05f0ef00757f0c)
- [Spring 공식 문서: 빈 스코프](https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html)

책은 Spring 3.1을 설명하지만 이 실행 예제는 Spring Framework 6.1.13을 사용한다. 비밀번호 필드는 책의 예제 구조를 따르며 운영 환경의 비밀번호 저장 방식으로 사용하면 안 된다.
