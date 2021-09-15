DOCKER_INTERVIEW_PROJECT := interview-project

APP_PROJECT := $(INTERVIEW_PROJECT)/app

.PHONY: dev-interview-build
dev-interview-build:
	HOST_UID=$(HOST_UID) \
	HOST_GID=$(HOST_GID) \
	APP_PROJECT=$(APP_PROJECT) \
	docker-compose -p $(DOCKER_INTERVIEW_PROJECT) -f dev-environment/docker-compose.yml build

.PHONY: dev-interview-run
dev-interview-run:
	HOST_UID=$(HOST_UID) \
	HOST_GID=$(HOST_GID) \
	APP_PROJECT=$(APP_PROJECT) \
	docker-compose -p $(DOCKER_INTERVIEW_PROJECT) -f dev-environment/docker-compose.yml up

.PHONY: dev-interview-clean
dev-interview-clean:
	HOST_UID=$(HOST_UID) \
	HOST_GID=$(HOST_GID) \
	APP_PROJECT=$(APP_PROJECT) \
	docker-compose -p $(DOCKER_INTERVIEW_PROJECT) -f dev-environment/docker-compose.yml down -v