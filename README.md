```yaml
version: '3'
services:
  opensearch:
    image: opensearchproject/opensearch:2.0.0
    container_name: opensearch
    restart: always
    environment:
    - "OPENSEARCH_JAVA_OPTS=-Xms512m -Xmx512m"
    - "discovery.type=single-node"
    - "DISABLE_SECURITY_PLUGIN=true"
    ulimits:
      memlock:
        soft: -1
        hard: -1
      nofile:
        soft: 65536
        hard: 65536
    ports:
    - 9200:9200
    - 9600:9600
    logging:
      options:
        max-file: '2'
        max-size: '32k'
    volumes:
    - ./data:/usr/share/opensearch/data
    - ./plugins:/usr/share/opensearch/plugins
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 512M
  dashboard:
    image: opensearchproject/opensearch-dashboards:2.0.0
    container_name: dashboard
    restart: always
    environment:
      DISABLE_SECURITY_DASHBOARDS_PLUGIN: true
      I18N_LOCALE: zh-CN
      OPENSEARCH_HOSTS: '["http://opensearch:9200"]'
    ports:
    - '5601:5601'
    depends_on:
    - opensearch
    logging:
      options:
        max-file: '2'
        max-size: '32k'
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 256M
```