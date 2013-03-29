BuildBox
======================================================
BuildBox is an Android-Application with the intention to give ROM developers a simple and easy way to provide updates, Addons and more to their users within a good looking and simple to use UI.

The content provided within BuildBox is completely remotely configured and done with json.
This approach is used to get highly modular configuration capabilities with as low as possible data throughput.

The used backend for BuildBox can be a simple json file or a webservice, basicaly every server with direct link capabilities (or simple redirects to the real content) can be used.

To configure where the applications backend is reachable (and which is the current ROM version if the update notification feature is used) build.prop properties can be used or the given resource strings within the app can be altered.

The buildbox java packages (and it's subpackages) are licensed under LGPL, dslv is licensed under the apache license, for more detail look at the [LICENSE.txt](https://github.com/tectas/buildbox/blob/dev/src/at/tectas/buildbox/LICENSE.txt) of the buildbox package or this [LICENSE.txt](https://github.com/tectas/buildbox/blob/dev/src/com/mobeta/android/dslv/LICENSE.txt) for dslv.

Main features
------------------------------------------------------

+ Tabbed UI
+ Nested lists within the tabs (in theory infinite levels of nesting [but for usability i wouldn't recommend more than 5 steps])
+ Detail views with many optional fields, like description, changelog, developers, images,...
+ Completely remotly configured content using json
+ Optional Md5 verfification
+ Download queue
+ Sorting of download queue by drag & drop
+ Concurrent downloads to achieve full use of bandwidth (configurable amount)
+ Backup and restore of download queues
+ Support of every host with direct link capabilities
+ Support of hosts with up to 5 redirects (note: still direct link redirects, download webpages don't get handled)
+ Internal retry or resume (if supported by the server) functionality to avoid broken downloads because of unstable connections (up to 5 retries)
+ Direct install of apks
+ "External" link handling
+ Direct flash capabilities (if an openrecoveryscript supporting recovery is installed)
+ Adding of zips from storage to the queue
+ Backup/wipe options before flashing
+ Queue filter to only flash Successful/Done (Successful=downloaded+md5sum correct, Done=donwloaded+no md5sum provided) downloads (md5mismatches can optional be taken in as well)
+ Update notifications
+ Configurable interval for update check
+ Update version filter to avoid multiple notifications per version
+ New version check on startup
+ Image recycling and scaling (if an image is used more than once [identified by it's url], it will be downloaded only once and simply reused, also it will be scaled to the fitting size of the view to take as less bandwidth and ram as possible)
+ more to come...
