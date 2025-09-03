const readline = require("readline");
const fs = require("fs");

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

rl.question("Mi a szöveg amit át akarsz alakítani? ", (txt) => {
  rl.question("Mi legyen a fájl neve amit ki akarsz írni? ", (filename) => {
    let chaos = txt.split("").map(char => {
      const code = char.charCodeAt(0);
      return String.fromCharCode(code + 1000); // eltoljuk a karaktereket magasabb unicode tartományba
    }).join("");

    fs.writeFileSync(filename, chaos); // <- itt volt a hiba

    console.log(`A fájl "${filename}" sikeresen létrejött. Tartalma: teljes káosz 😈`);
    rl.close();
  });
});
