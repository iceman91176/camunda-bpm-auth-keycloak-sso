DOCKER_CMD         ?= docker

.third-party-libs-$(CAMUNDA_RELEASE).tmp: third-party-libs/$(CAMUNDA_RELEASE)/pom.xml
	test -d tmp || mkdir tmp
	mvn dependency:copy-dependencies $(MVN_ARGS) -f third-party-libs/$(CAMUNDA_RELEASE)/pom.xml
	touch .third-party-libs-$(CAMUNDA_RELEASE).tmp

.engine-rest-libs-$(CAMUNDA_RELEASE).tmp: engine-rest-libs/$(CAMUNDA_RELEASE)/pom.xml
	test -d tmp || mkdir tmp
	mvn dependency:copy-dependencies $(MVN_ARGS) -f engine-rest-libs/$(CAMUNDA_RELEASE)/pom.xml
	touch .engine-rest-libs-$(CAMUNDA_RELEASE).tmp

.webapp-libs-$(CAMUNDA_RELEASE).tmp: webapp-libs/$(CAMUNDA_RELEASE)/pom.xml
	test -d tmp || mkdir tmp
	mvn dependency:copy-dependencies $(MVN_ARGS) -f webapp-libs/$(CAMUNDA_RELEASE)/pom.xml
	touch .webapp-libs-$(CAMUNDA_RELEASE).tmp

docker_build_tomcat-7.14.0: .third-party-libs-$(CAMUNDA_RELEASE).tmp .webapp-libs-$(CAMUNDA_RELEASE).tmp .engine-rest-libs-$(CAMUNDA_RELEASE).tmp
	$(DOCKER_CMD) build --build-arg CAMUNDA_RELEASE=$(CAMUNDA_RELEASE) . -t witcom/camunda-identity-keycloak:tomcat-7.14.0

clean:
	rm -rf .third-party-libs-$(CAMUNDA_RELEASE).tmp third-party-libs/$(CAMUNDA_RELEASE)/target
	rm -rf .engine-rest-libs-$(CAMUNDA_RELEASE).tmp engine-rest-libs/$(CAMUNDA_RELEASE)/target
	rm -rf .webapp-libs-$(CAMUNDA_RELEASE).tmp webapp-libs/$(CAMUNDA_RELEASE)/target
         
