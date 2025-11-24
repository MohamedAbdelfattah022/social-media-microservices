INSERT INTO PROPERTIES (APPLICATION, PROFILE, LABEL, KEY, VALUE)
VALUES ('application', 'default', 'master', 'logging.pattern.console',
        '%green(%d{HH:mm:ss.SSS}) %blue(%-5level) %red([%thread]) %yellow(%logger{15}) - %msg%n'),
       ('application', 'default', 'master', 'management.endpoints.web.exposure.include', '*');