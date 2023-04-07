FROM golang AS build-env
WORKDIR /build
COPY . /build
RUN go build -a -tags 'osusergo netgo static_build' -ldflags '-w -extldflags "-static"' -o app

FROM cgr.dev/chainguard/static
COPY --from=build-env /build/app /workspace/
WORKDIR /workspace
COPY /assets /workspace/assets
ENTRYPOINT ["/workspace/app"]