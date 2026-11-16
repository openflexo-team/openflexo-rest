# openflexo-rest — Backlog

Features and evolutions to develop in the REST technology adapter. Each entry says what problem it solves and when it is done. Bugs that
are known and not fixed go to `KNOWN_DEFECTS.md`, not here.

Identifiers are stable and never reused: `REST-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## FML runtime errors

### REST-F-1 — The REST adapter raises typed FML errors  ·  `DONE`

**Problem.** Part of `CORE-F-11` phase 6 (`openflexo-core/BACKLOG.md`): the REST adapter already raised its failures, but untyped
(`FMLExecutionException`, and `BadCredentialsException` under it); a REST call that could not connect escaped as an undeclared I/O
exception of REST-assured; a select of a REST entity without `@Get` endpoint returned null (an empty result).

**Done (2026-11-16).** Scope: the paths that FML execution goes through.
- `BadCredentialsException` (unresolvable secret, HTTP 401) extends `FMLTechnologyError`, as `HbnException` in openflexo-jdbc.
- `GetEndPoint` (the select of a REST entity): a condition that is not a simple equality, an HTTP status in error, a response that is not a
  JSON object or does not parse, and a call that fails (connection refused, unknown host, timeout) are `FMLTechnologyError`s naming the
  call and the access point; the values of the conditions evaluate through `FMLEvaluation`. An HTTP 404 still gives an empty result.
- `RestVirtualModelInstance.selectFlexoConceptInstances`: a concept that is not a REST entity, or has no `@Get` endpoint, is an
  `FMLTechnologyError` (it returned null); no connected access point an `FMLNullReferenceError`. The access point is loaded on demand
  (connecting the model slot does not load it): a loading failure is an `FMLTechnologyError`, a cancelled loading an `FMLCancellation`.
- Kept as it was: an HTTP 404 gives an empty result (a `select unique` gives null), a wrong URL template included. An unchecked exception
  of the call (a defect, or a URL template that does not fit its parameters) is not a technology error: it becomes an internal error.
- Roles: no move to `getFlexoActorOrFail` (planned in `CORE-F-11`): the actor reference of a REST role
  (`RestObjectActorReference.retrieveModellingElement`) is a stub returning null, so there is no reading to type.
- `CORE-D-73`: the factory of REST instances (`RestVirtualModelInstanceModelFactory.makeNewFlexoConceptInstance`) is a stub returning null;
  no creation scheme runs there, nothing to discard. Left as it is.

**Tests.** `TestRestErrorKinds` (rest-ta-test, no network): a select through an access point no server answers (`http://127.0.0.1:1`) is an
`FMLTechnologyError` (also when the access point was not loaded), through one whose secret cannot be resolved a
`BadCredentialsException`; each asserts the type, the behaviour frame and
the line, and that nothing but the SLF4J warning of the first REST call is printed to stderr.

Not covered by a test: HTTP 401 and other error statuses, a body that is not a JSON object, a condition that is not an equality, a
concept without `@Get` or not a REST entity.

**Verified (2026-11-16).** rest-ta-test 9 (6 run, 3 skipped: network or display) green through the openflexo-dev composite; ECJ compiles
rest-ta; no other repository depends on rest-ta. Reviewed by `openflexo-reviewer` (the access point not loaded, proven by a probe, is
fixed; its other findings are recorded here). Not checked in the running application.
