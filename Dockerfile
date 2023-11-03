FROM neo4j:5.7

LABEL com.docker.compose.config-hash="52c8d3a8025e97f3153aafba2f06f1d0e8a698ad61820d91bb8978247da7eb3f"
LABEL com.docker.compose.container-number="1"
LABEL com.docker.compose.depends_on=""
LABEL com.docker.compose.image="sha256:1c085cd5b6d7e70b080914e77965b3fb19a7e74513579b83575ee980c220f7c2"
LABEL com.docker.compose.oneoff="False"
LABEL com.docker.compose.project="neo4j"
LABEL com.docker.compose.project.config_files="/data/apps/neo4j/docker-compose.yml"
LABEL com.docker.compose.project.working_dir="/data/apps/neo4j"
LABEL com.docker.compose.service="neo4j"
LABEL com.docker.compose.version="2.18.1"

# Set environment variables
ENV NEO4J_AUTH=neo4j/AgZnuoJNMMI4Dp!
ENV APP_NAME=neo4j
ENV APP_DB_NEO4J_PASSWORD=AgZnuoJNMMI4Dp!
ENV APP_URL=3.211.100.108
ENV APP_HTTP_PORT=9001
ENV APP_NETWORK=websoft9
ENV APP_DB_NEO4J_USER=neo4j
ENV APP_DB_NEO4J_PORT=7687
ENV APP_VERSION=5.7
ENV APP_URL_REPLACE=false
ENV PATH=/var/lib/neo4j/bin:/opt/java/openjdk/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
ENV JAVA_HOME=/opt/java/openjdk
ENV NEO4J_SHA256=e13e94d8c8730f9525f30f98821ade79b349af1697d7ac94a8c3cc8b0273b734
ENV NEO4J_TARBALL=neo4j-community-5.7.0-unix.tar.gz
ENV NEO4J_EDITION=community
ENV NEO4J_HOME=/var/lib/neo4j

# Mount the Neo4j data volume
VOLUME /var/lib/neo4j

# Expose the Neo4j ports
EXPOSE 7473 7474 7687

# Start the Neo4j server
#CMD ["neo4j"]
CMD ["sleep", "inf"]
