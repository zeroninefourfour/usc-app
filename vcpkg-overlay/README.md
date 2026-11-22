# vcpkg overlay for the Android build

Kept next to the project instead of forking Drewol/vcpkg. Passed to the build
through the `-DVCPKG_OVERLAY_PORTS` / `-DVCPKG_OVERLAY_TRIPLETS` CMake options
(see `app/build.gradle`); the vcpkg submodule stays on a pristine upstream
commit (Drewol/vcpkg @ 42fd36190).

## ports/sdl2

Upstream port (Drewol/vcpkg) plus one Android fix in `portfile.cmake`:

- demote `-Werror=declaration-after-statement` — Android NDK clang fires
  `-Wdeclaration-after-statement` in every C mode, which breaks the build.

## triplets/arm64-android.cmake

Upstream community triplet (Drewol/vcpkg) plus:

- `VCPKG_LIBRARY_LINKAGE static` (upstream had `dynamic`) — the game
  statically links its dependencies; `VCPKG_CRT_LINKAGE` stays `dynamic`
  for libc++_shared.
