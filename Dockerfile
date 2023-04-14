FROM alpine
ARG TARGETOS
ARG TARGETARCH
ARG BINDIR
WORKDIR /workspace
COPY dist/ .
RUN export BINDIR=$(ls | grep dictionbot_$TARGETOS | grep $TARGETARCH) && mv $BINDIR/dictionbot ./app

FROM cgr.dev/chainguard/static
COPY --from=build-env /app /
ENTRYPOINT ["/app"]