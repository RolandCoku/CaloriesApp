document.addEventListener("DOMContentLoaded", () => {
    fetch("/food-entries/user/entries")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById("entriesTableBody");
            tableBody.innerHTML = ""; // Clear existing rows
            data.forEach(entry => {
                const newRow = `
                    <tr>
                        <td>${entry.name}</td>
                        <td>${entry.calories}</td>
                        <td>${entry.price.toFixed(2)}</td>
                        <td>${new Date(entry.createdAt).toLocaleDateString()}</td>
                    </tr>
                `;
                tableBody.insertAdjacentHTML("beforeend", newRow);
            });
        })
        .catch(error => console.error("Error fetching food entries:", error));

    fetch("/food-entries/user/total")
        .then(response => response.json())
        .then(data => {
            const totalCalories = document.getElementById("dayCalories");
            const totalPrice = document.getElementById("weaklyExpenditure");
            totalCalories.textContent = data.totalCalories;
            totalPrice.textContent = data.totalPrice.toFixed(2);
        })
        .catch(error => console.error("Error fetching total calories and price:", error));

});
