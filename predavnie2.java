// ==UserScript==
// @name        Predavac
// @description script to sell and withdraw items
// @include     *://*s17-sk.gladiatus.gameforge.*/game/index.php?mod=inventory&sub=*&subsub=*&*
// @include     *://*s17-sk.gladiatus.gameforge.*/game/index.php?mod=packages*
// @author      Gucci
// @version  	2.1
// @namespace   https://greasyfork.org/users/104906
// @grant GM_getValue
// @grant GM_setValue
// @grant GM_addStyle
// ==/UserScript==
var tlacidlo = false;
var mode;
var poslednyItem = null;
var link = window.location.href;
var button = document.createElement("BUTTON");

link = link.split("=");
if (link[1] == "inventory&sub") {
    mode = 'Predavaj!';
}
else mode = 'Vytahuj!';
// console.log(mode);

button.innerHTML = mode;
button.addEventListener("click", prepinanie);
document.getElementById("content").insertBefore(button, document.getElementById("content").firstChild);

function getRndInteger(min, max) {
    return Math.floor(Math.random() * (max - min + 1) ) + min;
}

function prepinanie() {
    if (tlacidlo) {
        tlacidlo = false;
    } else tlacidlo = true;
    // console.log(tlacidlo);

    if (tlacidlo) {
        if (mode == 'Predavaj!') {
            predavaj();
        } else {
            vytahuj();
        }
    }
}

function predavaj() {
    if (tlacidlo) {
        var cas, inventar, item, polozka;
        inventar = document.getElementById("inv");
        item = inventar.getElementsByClassName("ui-draggable");
        cas = getRndInteger( 600, 1000 );
        var evt = new Event('dblclick');
        setTimeout(dragToSell, cas);
    }
}

function vytahuj() {
    if (tlacidlo) {
        var item = document.getElementsByClassName('packageItem')[0];
        var evt = new Event('dblclick');
        var cas = getRndInteger( 1000, 1300 );
        if (item) {
            setTimeout(dragToPick, cas);
        } else {
            console.log("nic v balikoch");
            tlacidlo = false;
            setTimeout(prepniNaPredavanie(), cas);
        }
    }
}

function prepniNaPredavanie() {
    var predavacX;
    var cas = getRndInteger( 900, 1200 );
    predavacX = document.getElementById("submenu1").getElementsByClassName("menuitem")[3];
    setTimeout(function() {
        predavacX.click();
    }, cas);
}

function prepniObchodnika() {
    var actualObchodnik = window.location.href;
    var cas = getRndInteger( 900, 1200 );
    var celymMenom, poradie, predavacX;
    actualObchodnik = actualObchodnik.split("&");
    celymMenom = actualObchodnik[1];
    poradie = celymMenom.split("=");
    poradie = parseInt(poradie[1]);
    if (poradie > 0 && poradie < 6) {
        predavacX = document.getElementById("submenu1").getElementsByClassName("menuitem")[3 + poradie];
        setTimeout(function() {
            predavacX.click();
        }, cas);
    }
}

function prepniNaVykladnia() {
    var baliky;
    var cas = getRndInteger( 900, 1200 );
    baliky = document.getElementById("menue_packages");
    setTimeout(function() {
        baliky.click();
    }, cas);
}

function najdiVypln() {
    var inventar, item;
    var cas = getRndInteger( 600, 1000 );
    inventar = document.getElementById("inv");
    var nasiel = false;
    item = inventar.getElementsByClassName("ui-draggable");
    for (var i = 0; i < item.length; i++) {
        if (item.item(i).clientHeight == 32) {
            //setTimeout(dragToSell(i), cas);
            return i;
        }
    }
    return 0;
}

function najdiNaDolozenie() {
    var balik = document.getElementsByClassName('packageItem');
    var pocet = 0;
    for (var i = 0; i < balik.length; i++) {
        if (balik[i].childNodes[5].childNodes[0].clientHeight == 32) {
            return i;
        }
    }
    return 0;
}

function dragToPick(zaciatok = 0) {
    var evt = new Event('dblclick');
    var balik = document.getElementsByClassName('packageItem');
    var cas = getRndInteger(1000, 1300);
    if (balik.length > 0 && poslednyItem !== document.getElementsByClassName('packageItem')[zaciatok].childNodes[5].childNodes[0]) {
        console.log("Zase prace...");
        poslednyItem = document.getElementsByClassName('packageItem')[zaciatok].childNodes[5].childNodes[0];
        document.getElementsByClassName('packageItem')[zaciatok].childNodes[5].childNodes[0].dispatchEvent(evt);
        vytahuj()
    } else if (balik.length > 0) {
        console.log("Pozrem ci sa este nieco nemesti...");
        var poziciaVyplne = najdiNaDolozenie();
        if (poziciaVyplne > 0) {
            setTimeout(function() {
                if (balik.length > 0 && poslednyItem !== document.getElementsByClassName('packageItem')[poziciaVyplne].childNodes[5].childNodes[0]) {
                    poslednyItem = document.getElementsByClassName('packageItem')[poziciaVyplne].childNodes[5].childNodes[0];
                    document.getElementsByClassName('packageItem')[poziciaVyplne].childNodes[5].childNodes[0].dispatchEvent(evt);
                    dragToPick(poziciaVyplne);
                }
                else {
                    console.log("Plny inventar, idem k obchodnikovi.");
                    setTimeout(prepniNaPredavanie(), cas);
                }
            }, cas);
        } else {
            console.log("Plny inventar, idem k obchodnikovi.");
            setTimeout(prepniNaPredavanie(), cas);
        }
    } else {
        console.log("Plny inventar, idem k obchodnikovi.");
        setTimeout(prepniNaPredavanie(), cas);
    }
}

function dragToSell(zaciatok = 0) {
    var inventar, item, cas;
    inventar = document.getElementById("inv");
    item = inventar.getElementsByClassName("ui-draggable");
    cas = getRndInteger( 600, 1000 );
    var evt = new Event('dblclick');
    if (item.length > 0 && (document.getElementById("inv").getElementsByClassName("ui-draggable").item(zaciatok) !== poslednyItem)) {
        console.log("Zase prace...");
        poslednyItem = document.getElementById("inv").getElementsByClassName("ui-draggable").item(zaciatok);
        document.getElementById("inv").getElementsByClassName("ui-draggable").item(zaciatok).dispatchEvent(evt);
        predavaj();
    } else if (item.length > 0) {
        console.log("Pozrem ci sa este nieco nemesti...");
        var poziciaVyplne = najdiVypln();
        //setTimeout(najdiVypln(), cas);
        if (poziciaVyplne > 0) {
            setTimeout(function() {
                if (document.getElementById("inv").getElementsByClassName("ui-draggable").item(poziciaVyplne) !== poslednyItem) {
                    poslednyItem = document.getElementById("inv").getElementsByClassName("ui-draggable").item(poziciaVyplne);
                    document.getElementById("inv").getElementsByClassName("ui-draggable").item(poziciaVyplne).dispatchEvent(evt);
                    dragToSell(poziciaVyplne);
                }
                else {
                    console.log("Tento obchodnik plny, idem na nasledujuceho.");
                    setTimeout(prepniObchodnika(), cas);
                }
            }, cas);
        } else {
            console.log("Tento obchodnik plny, idem na nasledujuceho.");
            setTimeout(prepniObchodnika(), cas);
        }
    } else {
        console.log("Inventar je prazdny, ide do balikov.");
        setTimeout(prepniNaVykladnia(), cas);
    }
}