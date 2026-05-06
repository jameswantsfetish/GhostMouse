#define PY_SSIZE_T_CLEAN
#include <Python.h>
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

static void dirname_in_place(char *path) {
  char *slash = strrchr(path, '/');
  if (slash != NULL) {
    *slash = '\0';
  }
}

static int resolve_project_dir(char *buffer, size_t size, const char *argv0) {
  char executable[PATH_MAX];
  if (argv0[0] == '/') {
    snprintf(executable, sizeof(executable), "%s", argv0);
  } else {
    char cwd[PATH_MAX];
    if (getcwd(cwd, sizeof(cwd)) == NULL) {
      return -1;
    }
    snprintf(executable, sizeof(executable), "%s/%s", cwd, argv0);
  }

  char macos_dir[PATH_MAX];
  snprintf(macos_dir, sizeof(macos_dir), "%s", executable);
  dirname_in_place(macos_dir);

  char project_candidate[PATH_MAX];
  snprintf(project_candidate, sizeof(project_candidate), "%s/../../..", macos_dir);
  if (realpath(project_candidate, buffer) == NULL) {
    return -1;
  }
  return 0;
}

int main(int argc, char **argv) {
  (void)argc;
  char project_dir[PATH_MAX];
  if (resolve_project_dir(project_dir, sizeof(project_dir), argv[0]) != 0) {
    fprintf(stderr, "Could not resolve GhostMouse project directory.\n");
    return 1;
  }

  char log_path[PATH_MAX];
  snprintf(log_path, sizeof(log_path), "%s/ghostmouse-camera-sidecar.log", project_dir);
  freopen(log_path, "a", stdout);
  freopen(log_path, "a", stderr);
  setvbuf(stdout, NULL, _IONBF, 0);
  setvbuf(stderr, NULL, _IONBF, 0);

  if (chdir(project_dir) != 0) {
    perror("chdir");
    return 1;
  }

  Py_Initialize();

  char bootstrap[PATH_MAX * 3];
  snprintf(bootstrap, sizeof(bootstrap),
      "import sys\n"
      "sys.path.insert(0, %c%s/.venv/lib/python3.12/site-packages%c)\n"
      "sys.path.insert(0, %c%s%c)\n"
      "sys.argv = [%c%s/scripts/mediapipe_sidecar.py%c]\n",
      '"', project_dir, '"',
      '"', project_dir, '"',
      '"', project_dir, '"');
  if (PyRun_SimpleString(bootstrap) != 0) {
    Py_Finalize();
    return 1;
  }

  char script_path[PATH_MAX];
  snprintf(script_path, sizeof(script_path), "%s/scripts/mediapipe_sidecar.py", project_dir);
  FILE *script = fopen(script_path, "r");
  if (script == NULL) {
    perror("fopen");
    Py_Finalize();
    return 1;
  }

  int result = PyRun_SimpleFile(script, script_path);
  fclose(script);
  Py_Finalize();
  return result == 0 ? 0 : 1;
}
