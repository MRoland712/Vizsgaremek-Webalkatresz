const bcrypt = require("bcrypt");
const { endianness } = require("os");
const { exitCode } = require("process");
const readline = require("readline");

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});
rl.question("Text: ", (text) => {
    console.log(bcrypt.hashSync(text, 10));
    process.exit();
});