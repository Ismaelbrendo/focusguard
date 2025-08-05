package com.example.focusguard;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

// Classe especializada em analisar (fazer o "parse") de um pacote de DNS.
public class DnsPacket {

    public DnsHeader header;
    public List<DnsQuestion> questions;

    private DnsPacket() {}

    // Método principal que converte os bytes brutos num objeto DnsPacket organizado.
    public static DnsPacket fromBytes(ByteBuffer buffer) {
        if (buffer.remaining() < 12) return null; // Um cabeçalho DNS tem no mínimo 12 bytes

        DnsPacket packet = new DnsPacket();
        packet.header = DnsHeader.fromBytes(buffer);
        packet.questions = new ArrayList<>();

        // Lê cada "pergunta" (query) dentro do pacote DNS
        for (int i = 0; i < packet.header.qdcount; i++) {
            DnsQuestion question = DnsQuestion.fromBytes(buffer);
            if (question == null) return null; // Se houver erro na leitura, descarta o pacote
            packet.questions.add(question);
        }
        return packet;
    }

    // Representa o cabeçalho do pacote DNS
    public static class DnsHeader {
        public int id;
        public int flags;
        public int qdcount; // Número de perguntas

        public static DnsHeader fromBytes(ByteBuffer buffer) {
            DnsHeader header = new DnsHeader();
            header.id = buffer.getShort() & 0xFFFF;
            header.flags = buffer.getShort() & 0xFFFF;
            header.qdcount = buffer.getShort() & 0xFFFF;
            // Ignora os outros campos do cabeçalho que não nos interessam
            buffer.getShort(); // ancount
            buffer.getShort(); // nscount
            buffer.getShort(); // arcount
            return header;
        }
    }

    // Representa a pergunta DNS (ex: "qual o IP de youtube.com?")
    public static class DnsQuestion {
        public String domain;
        public int type;
        public int clazz;

        public static DnsQuestion fromBytes(ByteBuffer buffer) {
            DnsQuestion question = new DnsQuestion();
            question.domain = parseDomain(buffer);
            if (question.domain == null) return null;

            // Garante que há bytes suficientes para ler o tipo e a classe
            if (buffer.remaining() < 4) return null;
            question.type = buffer.getShort() & 0xFFFF;
            question.clazz = buffer.getShort() & 0xFFFF;
            return question;
        }

        // Método inteligente para ler o nome do domínio, que é codificado de forma especial
        private static String parseDomain(ByteBuffer buffer) {
            StringBuilder domain = new StringBuilder();
            while (true) {
                if (!buffer.hasRemaining()) return null;
                byte length = buffer.get();
                if (length == 0) break; // Fim do domínio
                if ((length & 0xC0) == 0xC0) { // É um ponteiro (compressão de nome)
                    // Para simplificar, não vamos seguir ponteiros, apenas paramos a leitura aqui.
                    if (!buffer.hasRemaining()) return null;
                    buffer.get(); // Apenas consome o segundo byte do ponteiro e para
                    break;
                }
                if (buffer.remaining() < length) return null;
                byte[] label = new byte[length];
                buffer.get(label);
                if (domain.length() > 0) {
                    domain.append('.');
                }
                domain.append(new String(label));
            }
            return domain.toString();
        }
    }
}
