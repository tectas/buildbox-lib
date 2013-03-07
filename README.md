BuildBox
======================================================
BuildBox is an Android-Application with the intention to give ROM developers a simple and easy way to provide updates, Addons and more to their users within a good looking and simple to use UI.

The content provided within BuildBox is completely remotely configured and done with json.
This approach is used to get highly modular configuration capabilities with as low as possible data throughput.

The used backend for BuildBox can be a simple json file or a webservice, basicaly every server with direct link capabilities (or simple redirects to the real content) can be used.

To configure where the applications backend is reachable (and which is the current ROM version if the update notification feature is used) build.prop properties can be used or the given resource strings within the app can be altered.

Main features
------------------------------------------------------

1.  Update notification capabilities
2.  Remotely configured content
3.  Tabbed UI
4.  Nested lists within the tabs (in theory infinite levels of nesting)
5.  Download queue
6.  Concurrent downloads
7.  OpenRecoveryScript support
8.  Redirect support for download urls (up to 5 times)
9.  Queue filter to only flash successful downloads
10. Md5sum verification
