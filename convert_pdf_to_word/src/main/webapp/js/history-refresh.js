function autoRefreshHistory() {
    setInterval(function () {
        // Không refresh khi tab bị ẩn (tiết kiệm tài nguyên)
        if (document.hidden) return;

        fetch(window.location.href, { cache: "no-store" })
            .then(res => res.text())
            .then(html => {
                const parser = new DOMParser();
                const newDoc = parser.parseFromString(html, "text/html");

                const newTable = newDoc.querySelector("table");
                const oldTable = document.querySelector("table");

                if (newTable && oldTable) {
                    oldTable.innerHTML = newTable.innerHTML;
                }
            })
            .catch(err => console.error("Refresh error:", err));
    }, 3000);
}

document.addEventListener("DOMContentLoaded", autoRefreshHistory);
