# 토비의 스프링 3.1 · 1.4 DaoFactory 실습

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
