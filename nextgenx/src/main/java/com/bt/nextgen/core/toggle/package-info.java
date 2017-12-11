/**
 * Classes to assist with the hiding/showing of global, release-level features that need to sit alongside production
 * code but be dormant until the designated release occurs. The idea is that these features are part of a long-running
 * project that <i>must not</i> be seen in production, but can be toggled to be visible/active in configured development
 * and test environments whilst the project is under development.
 * <p>Ideally, these toggles would not be needed at all if we only ever worked on a single project/feature at a time,
 * but Panorama is what it is, and having multiple projects running in parallel (that therefore need to co-exist
 * peacefully together in the same codebase) is the norm.
 * @link http://martinfowler.com/bliki/FeatureToggle.html
 */
package com.bt.nextgen.core.toggle;
