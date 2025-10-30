
// Copia password generata al click
const testo= document.getElementById("pwd");
const messaggio = document.getElementById("message");

testo.addEventListener("click", () => {

      // Copia il contenuto del paragrafo
      navigator.clipboard.writeText(testo.textContent)
        .then(() => {
          messaggio.innerHTML = ("<p style='margin:0px; color:green; font-weight: 700;'  >Testo copiato negli appunti!</p>");
        })
        .catch(err => {
          console.error("Errore durante la copia: " + err);
        });
    });