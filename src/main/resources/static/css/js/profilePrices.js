const ws = new WebSocket('wss://ws.kraken.com/v2');

ws.onopen = () => {
    if (typeof userHoldingsSymbols === 'undefined' || userHoldingsSymbols.length === 0) {
        console.warn("No crypto symbols in holdings to subscribe to.");
        return;
    }

    const krakenSymbols = userHoldingsSymbols.map(symbol => `${symbol}/USD`);

    ws.send(JSON.stringify({
        "method": "subscribe",
        "params": {
            "channel": "ticker",
            "symbol": krakenSymbols
        },
        "req_id": 1
    }));

    console.log("Subscription request sent for holdings symbols:", krakenSymbols);
};

ws.onmessage = (event) => {
    const data = JSON.parse(event.data);

    if (data.channel === "ticker" && data.data && data.data.length > 0) {
        const tickerData = data.data[0];
        const fullSymbol = tickerData.symbol;
        const baseSymbol = fullSymbol.split("/")[0];

        const price = tickerData.last;
        updateProfilePrice(baseSymbol, price);
    }
    else if (data.method === "subscribe" && data.success) {
        if (data.result && data.result.symbol) {
             console.log(`Successfully subscribed to ticker for: ${data.result.symbol}`);
        } else {
             console.log(`Successfully subscribed to ticker channel (acknowledgement for req_id: ${data.req_id})`);
        }
    } else if (data.event === "heartbeat") {
    } else if (data.error) {
        console.error("WebSocket Error:", data.error);
    } else {
        console.log("Unhandled WebSocket message:", data);
    }
};

function updateProfilePrice(symbol, price) {
    const priceCell = document.getElementById(`price-${symbol}`);
    if (priceCell) {
        priceCell.textContent = `$${parseFloat(price).toFixed(2)}`;
    }
}

function sellCrypto(symbol) {
    const amountInput = document.getElementById(`amountToSell-${symbol}`);
    const cryptoQuantity = parseFloat(amountInput.value);

    if (isNaN(cryptoQuantity) || cryptoQuantity <= 0) {
        alert("Please enter a valid crypto quantity to sell.");
        return;
    }

    const holdingAmountCell = document.getElementById(`holdingAmount-${symbol}`);
    const holdingAmount = parseFloat(holdingAmountCell.textContent);

    if (cryptoQuantity > holdingAmount) {
        alert(`You don't have enough ${symbol} to sell. You currently hold ${holdingAmount}.`);
        return;
    }

    const priceCell = document.getElementById(`price-${symbol}`);
    const priceText = priceCell ? priceCell.textContent : "$0.00";
    const pricePerCoin = parseFloat(priceText.replace("$", ""));

    if (isNaN(pricePerCoin) || pricePerCoin <= 0) {
        alert("Could not retrieve live price for selling. Please try again.");
        console.error("Invalid price extracted for selling symbol:", symbol, "Price text:", priceText);
        return;
    }

    const totalPriceUSD = cryptoQuantity * pricePerCoin;

    if (!confirm(`Are you sure you want to sell ${cryptoQuantity.toFixed(8)} ${symbol} for an estimated $${totalPriceUSD.toFixed(2)}?`)) {
        return;
    }

    fetch(`/sell/${symbol}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `cryptoQuantity=${cryptoQuantity}&pricePerCoin=${pricePerCoin}`
    })
    .then(response => {
        if (response.redirected) {
            window.location.href = response.url;
        } else if (response.ok) {
            alert("Sell order successful!");
            amountInput.value = "";
            window.location.reload();
        } else {
            response.text().then(msg => alert(`Sell order failed: ${msg}`));
        }
    })
    .catch(error => {
        console.error("Error selling crypto:", error);
        alert("Something went wrong. Please try again.");
    });
}


function confirmReset() {
    if (confirm("Are you sure you want to reset your account? This will delete all holdings and restore your balance to $10,000.")) {
        fetch("/reset-account", {
            method: "POST"
        })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url;
            } else {
                alert("Something went wrong. Please try again.");
            }
        })
        .catch(error => {
            console.error("Reset failed:", error);
            alert("Something went wrong. Please try again.");
        });
    }
}
