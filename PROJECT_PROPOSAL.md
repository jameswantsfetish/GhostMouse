# GhostMouse Project Proposal

**Project Title:** GhostMouse  
**Project Owner:** James Mamhiyo  
**Repository:** https://github.com/jameswantsfetish/GhostMouse  
**Project Type:** Desktop accessibility, gesture control, computer vision  

## Executive Summary

GhostMouse is a desktop application that lets users control their computer mouse using hand gestures captured from a webcam. The project combines Java, MediaPipe, OpenCV, and a lightweight socket bridge to translate real-time hand landmarks into mouse movement, clicks, dragging, and scrolling.

The goal of GhostMouse is to make hands-free computer control more accessible, practical, and understandable. It is designed with a safe demo mode, clear setup documentation, automated tests, and a maintainable architecture so users and contributors can understand how the system works and improve it over time.

## Problem Statement

Traditional mouse input requires physical contact with a mouse, trackpad, or touchscreen. This can be limiting for people who want hands-free control, are experimenting with alternative interfaces, have temporary mobility limitations, or want to explore computer vision as a practical desktop tool.

Many gesture-control projects are either difficult to set up, too experimental for normal users, or tightly coupled to one platform. GhostMouse addresses this by separating computer vision from desktop control, documenting setup clearly, and keeping the main gesture logic testable.

## Proposed Solution

GhostMouse uses a webcam to detect hand landmarks and converts those landmarks into mouse actions:

- Palm movement controls cursor position.
- Thumb-index pinch triggers left click.
- Thumb-pinky pinch triggers right click.
- Thumb-index-middle pinch performs drag.
- Index-middle gesture activates scrolling.

The system uses a Python MediaPipe sidecar for webcam hand tracking and a Java desktop application for gesture interpretation, smoothing, UI feedback, and OS mouse control.

## Objectives

- Build a working desktop gesture-control mouse system.
- Provide safe demo mode so users can test the app without moving the real cursor.
- Support real-time hand landmark streaming from a webcam.
- Implement smooth cursor movement with reduced jitter.
- Implement click, right-click, drag, and scroll gestures.
- Document setup clearly for macOS, Windows, and Linux.
- Maintain the project with tests, CI, issue templates, and contribution guidelines.

## Scope

### In Scope

- Java desktop application.
- Python MediaPipe webcam sidecar.
- Local TCP landmark protocol.
- Gesture recognition and pointer smoothing.
- Swing debug/control window.
- Unit tests for math, mapping, and gesture logic.
- GitHub Actions CI across Windows, macOS, and Ubuntu.
- Documentation for setup, architecture, troubleshooting, and Windows use.

### Out Of Scope For Initial Release

- Mobile app support.
- Cloud-based gesture processing.
- Multi-hand gesture control.
- Full installer for every operating system.
- Advanced machine-learning model training.
- Production-grade accessibility certification.

## Target Users

- Students and developers learning computer vision and desktop automation.
- Users interested in hands-free computer control.
- Makers building experimental human-computer interaction projects.
- Accessibility-focused developers exploring alternative input systems.

## Technical Approach

GhostMouse is built around a simple architecture:

```text
Webcam -> Python MediaPipe sidecar -> TCP landmarks -> Java gesture engine -> OS mouse
```

### Java Application

The Java application manages:

- Configuration and command-line options.
- Demo and socket landmark sources.
- Gesture analysis.
- Cursor movement and click actions.
- Swing UI and optional system tray.
- Unit-tested math and gesture behavior.

### Python Sidecar

The Python sidecar manages:

- Webcam access with OpenCV.
- Hand landmark detection with MediaPipe.
- Landmark encoding.
- TCP streaming to the Java application.

### Testing And Maintenance

The project includes:

- Maven-based Java test suite.
- GitHub Actions CI for Windows, macOS, and Ubuntu.
- Dependabot configuration.
- Issue and pull request templates.
- MIT license and contribution guidelines.

## Tools And Technologies

- **Java 21:** Main desktop application.
- **Maven:** Build and dependency management.
- **Swing:** Desktop UI.
- **Java AWT Robot:** Real mouse movement and clicks.
- **Python 3:** Camera sidecar.
- **MediaPipe:** Hand landmark detection.
- **OpenCV:** Webcam input.
- **GitHub Actions:** Continuous integration.
- **GitHub:** Version control, documentation, and project hosting.

## Expected Deliverables

- Public GitHub repository.
- Working Java desktop app.
- Python webcam sidecar.
- Runnable demo mode.
- Gesture-controlled mouse mode.
- README and setup documentation.
- Troubleshooting and architecture documentation.
- Automated CI checks.
- Project proposal and changelog.

## Timeline

| Phase | Description | Estimated Time |
| --- | --- | --- |
| Phase 1 | Research MediaPipe hand tracking and desktop mouse control | 1 week |
| Phase 2 | Build Java app structure, config, and demo mode | 1 week |
| Phase 3 | Add gesture recognition and pointer smoothing | 1-2 weeks |
| Phase 4 | Build Python MediaPipe sidecar and socket protocol | 1 week |
| Phase 5 | Add UI, tray support, launch scripts, and safety controls | 1 week |
| Phase 6 | Add tests, documentation, CI, and GitHub maintenance files | 1 week |
| Phase 7 | Polish, test on multiple platforms, and prepare public release | 1 week |

## Risks And Mitigation

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Webcam access fails on some systems | Users cannot use real tracking | Provide demo mode and troubleshooting docs |
| Cursor movement feels jittery | Poor user experience | Use adaptive smoothing and active-zone calibration |
| Click gestures trigger accidentally | Unsafe or frustrating behavior | Include `--disable-clicks` calibration mode |
| Platform differences affect setup | Confusing installation | Provide separate Windows and macOS/Linux instructions |
| Java Robot permissions are restricted | Real mouse control may fail | Document OS permissions and safe test modes |

## Success Criteria

The project will be considered successful when:

- The app builds and tests successfully on Windows, macOS, and Ubuntu.
- A user can run demo mode from the README instructions.
- A user can connect the Python sidecar and stream webcam landmarks.
- Cursor movement and core gestures work in real-time.
- The repository is understandable, documented, and ready for contributors.

## Future Improvements

- Add a cross-platform installer.
- Add a calibration screen in the UI.
- Add configurable gesture profiles.
- Add support for multiple cameras.
- Add visual gesture feedback and onboarding.
- Add automated sidecar launch support for Windows.
- Explore direct Java or native MediaPipe integration.

## Conclusion

GhostMouse demonstrates how computer vision can become a practical desktop input tool. By combining MediaPipe hand tracking with a Java gesture engine, the project creates a clear, extensible foundation for hands-free mouse control.

The project is valuable both as a working tool and as a learning platform for accessibility, human-computer interaction, computer vision, and desktop automation.
