FROM alpine AS build-env
ARG TARGETARCH
WORKDIR /build
COPY ./*.zip .
RUN unzip $TARGETARCH.zip

FROM cgr.dev/chainguard/static
COPY --from=build-env /build/dictionbot /dictionbot
ENTRYPOINT ["/dictionbot"]