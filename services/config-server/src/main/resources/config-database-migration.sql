INSERT INTO PROPERTIES (APPLICATION, PROFILE, LABEL, KEY, VALUE)
VALUES ('application', 'default', 'master', 'logging.pattern.console',
        '%green(%d{HH:mm:ss.SSS}) %blue(%-5level) %red([%thread]) %yellow(%logger{15}) - %msg%n'),
       ('application', 'default', 'master', 'management.endpoints.web.exposure.include', '*');


INSERT INTO PROPERTIES (APPLICATION, PROFILE, LABEL, KEY, VALUE)
VALUES ('application', 'default', 'master', 'eureka.instance.prefer-ip-address', 'true'),
       ('application', 'default', 'master', 'eureka.client.fetch-registry', 'true'),
       ('application', 'default', 'master', 'eureka.client.service-url.defaultZone', 'http://localhost:8761/eureka/');


INSERT INTO PROPERTIES (APPLICATION, PROFILE, LABEL, KEY, VALUE)
VALUES ('discovery-service', 'default', 'master', 'HOST_NAME', 'localhost'),
       ('discovery-service', 'default', 'master', 'PORT', '8761'),
       ('discovery-service', 'default', 'master', 'ZONE', 'http://${eureka.instance.hostname}:${server.port}/eureka/');
