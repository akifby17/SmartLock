const WebSocket = require('ws');

// WebSocket sunucusu oluştur
const wss = new WebSocket.Server({ port:8080});

wss.on('connection', (ws) => {
    console.log('Yeni bir istemci bağlandı');

    // İstemciden gelen mesajı dinle
    ws.on('message', (message) => {
       // console.log('Gelen mesaj:', message);

        // Mesajı diğer istemcilere gönder
        wss.clients.forEach((client) => {
            if (client !== ws && client.readyState === WebSocket.OPEN) {
                client.send(message); // Base64 görüntü verisini gönder
            }
        });
    });

    ws.on('close', () => {
        console.log('İstemci bağlantısı kapandı');
    });
});

console.log('WebSocket sunucusu 8080 portunda çalışıyor...');