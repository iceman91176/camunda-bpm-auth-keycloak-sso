DOCKER_CMD         ?= docker
CAMUNDA_RELEASE ?= 7.14.x
DOCKER_REPO ?= some-repo.org 
DOCKER_ORG ?= witcom
DOCKER_RELEASE ?= 1.2

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

docker_build: .third-party-libs-$(CAMUNDA_RELEASE).tmp .webapp-libs-$(CAMUNDA_RELEASE).tmp .engine-rest-libs-$(CAMUNDA_RELEASE).tmp
	$(DOCKER_CMD) build --build-arg CAMUNDA_RELEASE=$(CAMUNDA_RELEASE) . -t camunda-$(CAMUNDA_RELEASE)-identity-keycloak:$(DOCKER_RELEASE)

docker_tag:
	$(DOCKER_CMD) tag camunda-$(CAMUNDA_RELEASE)-identity-keycloak:$(DOCKER_RELEASE) $(DOCKER_REPO)/$(DOCKER_ORG)/camunda-$(CAMUNDA_RELEASE)-identity-keycloak:$(DOCKER_RELEASE)
	$(DOCKER_CMD) tag camunda-$(CAMUNDA_RELEASE)-identity-keycloak:$(DOCKER_RELEASE) $(DOCKER_REPO)/$(DOCKER_ORG)/camunda-$(CAMUNDA_RELEASE)-identity-keycloak:latest

clean:
	rm -rf .third-party-libs-$(CAMUNDA_RELEASE).tmp third-party-libs/$(CAMUNDA_RELEASE)/target
	rm -rf .engine-rest-libs-$(CAMUNDA_RELEASE).tmp engine-rest-libs/$(CAMUNDA_RELEASE)/target
	rm -rf .webapp-libs-$(CAMUNDA_RELEASE).tmp webapp-libs/$(CAMUNDA_RELEASE)/target
         
