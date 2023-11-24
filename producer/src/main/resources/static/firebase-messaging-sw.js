importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-messaging.js');
importScripts('firebaseClientKey.js');

firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();
messaging.onBackgroundMessage((payload) => {
    console.log(
      '[firebase-messaging-sw.js] Received background message ',
      payload
    );
    // Customize notification here
    const notificationTitle = payload.data.title;
    const notificationOptions = {
      body: payload.data.content,
      icon: '/favicon.ico'
    };

    self.registration.showNotification(notificationTitle, notificationOptions);
});

