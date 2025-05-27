const ws = new WebSocket('wss://ws.kraken.com/v2');
const tableBody = document.getElementById('crypto-body');

const symbolNames = {
    "BTC/USD": "Bitcoin",
    "ETH/USD": "Ethereum",
    "ADA/USD": "Cardano",
    "XRP/USD": "Ripple",
    "SOL/USD": "Solana",
    "DOGE/USD": "Dogecoin",
    "DOT/USD": "Polkadot",
    "LTC/USD": "Litecoin",
    "SNX/USD": "Synthetix",
    "COMP/USD": "Compound",
    "LINK/USD": "Chainlink",
    "AVAX/USD": "Avalanche",
    "TRX/USD": "Tron",
    "ATOM/USD": "Cosmos",
    "XLM/USD": "Stellar",
    "ETC/USD": "Ethereum Classic",
    "FIL/USD": "Filecoin",
    "EOS/USD": "EOS",
    "AAVE/USD": "Aave",
    "UNI/USD": "Uniswap"
};

ws.onopen = () => {
    ws.send(JSON.stringify({
        "method": "subscribe",
        "params": {
            "channel": "ticker",
            "symbol": Object.keys(symbolNames)
        },
        "req_id":1
    }));
};

ws.onmessage = (event) => {
    const data = JSON.parse(event.data);

    if (data.channel === "ticker" && data.data && data.data.length > 0) {
        const tickerData = data.data[0];

        const symbol = tickerData.symbol;
        const price = tickerData.last;
        updateTable(symbol, price);
    }
    else if (data.method === "subscribe" && data.success) {
        console.log(`Successfully subscribed to: ${data.result.symbol}`);
    } else if (data.event === "heartbeat") {
    } else if (data.error) {
        console.error("WebSocket Error:", data.error);
    } else {
        console.log("Unhandled message:", data);
    }
};

function updateTable(symbol, price) {
    let row = document.getElementById(symbol);

    if (!row) {
        row = document.createElement('tr');
        row.id = symbol;
        row.innerHTML = `
            <td>${symbol}</td>
            <td>${symbolNames[symbol]}</td>
            <td class="price">$${parseFloat(price).toFixed(2)}</td>
            <td>
                <input type="number" id="amount-${symbol}" min="1">
            </td>
            <td>
                <button onclick="buyCrypto('${symbol}')">Buy</button>
            </td>
        `;
        tableBody.appendChild(row);
    } else {
        row.querySelector(".price").textContent = `$${parseFloat(price).toFixed(2)}`;
    }
}

function buyCrypto(symbol) {
     console.log("buyCrypto: Function called with symbol:", symbol);

     const amountInput = document.getElementById(`amount-${symbol}`);
     if (!amountInput) {
         console.error(`buyCrypto: Amount input element not found for symbol: amount-${symbol}`);
         alert("An error occurred: Could not find the quantity input. Please refresh.");
         return;
     }
     console.log("buyCrypto: amountInput element:", amountInput);

     const cryptoQuantity = parseFloat(amountInput.value);
     console.log("buyCrypto: cryptoQuantity parsed from input:", cryptoQuantity);

     if (isNaN(cryptoQuantity) || cryptoQuantity <= 0) {
         alert("Please enter a valid crypto quantity.");
         console.error("buyCrypto: Invalid cryptoQuantity, returning.");
         return;
     }

     const rowElement = document.getElementById(symbol);
     if (!rowElement) {
         console.error(`buyCrypto: Table row element not found for symbol: ${symbol}`);
         alert("An error occurred: Could not find the crypto data row. Please refresh.");
         return;
     }

     const priceCell = rowElement.querySelector(".price");
     if (!priceCell) {
         console.error(`buyCrypto: Price cell not found within row for symbol: ${symbol}`);
         alert("An error occurred: Could not retrieve current price. Please try again.");
         return;
     }

     const priceText = priceCell.textContent;
     console.log("buyCrypto: priceText extracted:", priceText);

     const pricePerCoin = parseFloat(priceText.replace("$", ""));
     console.log("buyCrypto: pricePerCoin parsed:", pricePerCoin);

     if (isNaN(pricePerCoin) || pricePerCoin <= 0) {
         alert("Could not retrieve current price. Please try again.");
         console.error("buyCrypto: Invalid pricePerCoin, returning.");
         return;
     }

     const totalPriceUSD = cryptoQuantity * pricePerCoin;
     console.log("buyCrypto: calculated totalPriceUSD:", totalPriceUSD);

     const balanceInput = document.getElementById("hidden-user-balance");
     if (!balanceInput) {
         console.error("buyCrypto: Hidden user balance element not found.");
         alert("An error occurred: Could not retrieve your balance. Please refresh.");
         return;
     }
     console.log("buyCrypto: balanceInput element:", balanceInput);

     let currentBalance = parseFloat(balanceInput.value);
     console.log("buyCrypto: currentBalance:", currentBalance);

     if (isNaN(currentBalance)) {
         alert("Your balance could not be read. Please refresh the page.");
         console.error("buyCrypto: User balance is NaN.");
         return;
     }

     if (totalPriceUSD > currentBalance) {
         alert(`Insufficient funds. You have $${currentBalance.toFixed(2)} and need $${totalPriceUSD.toFixed(2)}.`);
         console.error("buyCrypto: Insufficient funds, returning.");
         return;
     }

     console.log("buyCrypto: Attempting to show confirm dialog.");
     if (!confirm(`Do you want to buy ${cryptoQuantity.toFixed(8)} ${symbol.split('/')[0]} for $${totalPriceUSD.toFixed(2)}?`)) {
         console.log("buyCrypto: User cancelled purchase.");
         return;
     }
     console.log("buyCrypto: User confirmed purchase. Proceeding with fetch.");

     const baseUrlSymbol = symbol.split('/')[0];

     fetch(`/buy/${baseUrlSymbol}`, {
         method: "POST",
         headers: {
             "Content-Type": "application/x-www-form-urlencoded"
         },
         body: `cryptoQuantity=${cryptoQuantity}&pricePerCoin=${pricePerCoin}`
     })
     .then(response => {
         console.log("buyCrypto: Fetch response received. Status:", response.status, "OK status:", response.ok);
         if (!response.ok) {
             return response.text().then(errorMessage => {
                 throw new Error(`Server responded with status ${response.status}: ${errorMessage}`);
             });
         }
         return response.text();
     })
     .then(message => {
         alert(message);
         console.log("buyCrypto: Purchase successful, attempting to reload page...");
         window.location.reload();
     })
     .catch(error => {
         console.error("buyCrypto: Fetch error:", error);
         alert(`Purchase failed: ${error.message || "Something went wrong. Please try again."}`);
     });
 }





