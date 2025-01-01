const WebSocket = require('ws');

// WebSocket sunucusunu başlat
const wss = new WebSocket.Server({ port: 9999 }, () => {
    console.log('WebSocket sunucusu 9999  portunda çalışıyor.');
});

// Bağlı istemcileri saklamak için bir Set
const clients = new Set();

wss.on('connection', (ws) => {
    console.log('Yeni bir istemci bağlandı.');
    clients.add(ws);

    // İstemciden gelen mesajları dinle
    ws.on('message', (message) => {
        if (Buffer.isBuffer(message)) {
            // Gelen mesaj binary formatında ise
            console.log('Binary mesaj alındı:', message.toString('hex')); // Hex formatında logla
        } else {
            // Gelen mesaj metin (string) formatında ise
            console.log('Metin mesajı alındı:', message);
        }

        // Mesajı tüm diğer istemcilere yayınla
        for (const client of clients) {
            if (client !== ws && client.readyState === WebSocket.OPEN) {
                 message=message.toString('utf-8');
                client.send(message); // Binary ya da metin fark etmeksizin iletir
            }
        }
    });
    // İstemci bağlantısını kapattığında
    ws.on('close', () => {
        console.log('Bir istemci bağlantısını kapattı.');
        clients.delete(ws);
    });

    // Hata durumlarını yönet
    ws.on('error', (error) => {
        console.error(`Hata oluştu: ${error.message}`);
    });
});

console.log('WebSocket sunucusu başlatıldı.');