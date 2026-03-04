import { spawn } from "child_process";
import path from "path";

process.chdir(path.join(import.meta.dirname, "apps/web"));
const child = spawn(
  "./node_modules/.bin/eleventy",
  ["--serve", "--port=8080"],
  { stdio: "inherit" }
);
child.on("exit", (code) => process.exit(code));
