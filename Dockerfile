FROM cgr.dev/chainguard/static
ARG TARGETARCH
COPY ./dist/dictionbot_linux_${TARGETARCH}*/ /
WORKDIR /data
ENTRYPOINT ["/dictionbot"]