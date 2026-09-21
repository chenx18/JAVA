# 第八章 DNS、TCP、TLS、HTTPS

## 一、本章具体知识点

- DNS 解析
- Recursive Resolver
- TCP 连接
- 三次握手
- 四次挥手
- sequence/ACK
- retransmission
- congestion control
- TLS Handshake
- Certificate
- CA
- HTTPS

## 二、各知识点详细解释

典型 HTTPS 访问可抽象成：

```text
URL
→ DNS
→ IP
→ TCP / QUIC
→ TLS
→ HTTP
```

TCP 提供可靠、有序字节流；TLS 在安全连接中提供认证、机密性和完整性。

### 三次握手

核心目的是双方确认通信能力、同步初始序列号等。

### TLS

客户端验证服务器证书，通过密钥协商建立后续安全通信使用的密钥材料。

## 三、本章面试题与答案

### 题：HTTPS 为什么安全？

**答案：**

HTTPS 是 HTTP over TLS。TLS 通过证书认证通信对端身份，并通过密钥协商建立安全会话，提供传输机密性和完整性，减少窃听和篡改风险。

### 题：TCP 三次握手为什么不是两次？

**答案：**

两次无法完整确认双方的发送与接收能力以及客户端对服务器握手响应的确认。第三步让服务器知道客户端已经收到并准备好，使双方状态建立更加完整。

---
