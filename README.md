# Charity Donation System

A JavaFX-based charity donation management system with role-based dashboards for **Donors**, **Fundraisers**, and *
*Admins**. Built using `Java`, `JavaFX`, and `SQLite` via `JDBC`.

Built as a university project

---

## 📦 Features

### ✅ Donor

- Browse active campaigns and donate
- Track donation history with charts
- Subscribe to campaigns and receive notifications
- Update personal profile

### ✅ Fundraiser

- Create and manage fundraising campaigns
- View donations per campaign (with bar chart)
- Export campaign reports to CSV
- Receive notifications for donations and goal completions

### ✅ Admin

- Manage users (add/update/delete roles)
- View system-wide donation stats
- Generate reports (CSV + visual charts)
- Monitor campaign performance

---

## 🛠️ Tech Stack

- **Java 21+**
- **JavaFX (No FXML - pure Java layout)**
- **SQLite** with JDBC
- **Maven** for dependency and build management

---

## 🚀 Build & Run Instructions

### 1. Clone the Repo

```bash
git clone https://github.com/your-name/charity-donation-system.git
cd charity-donation-system
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the App

```bash
mvn javafx:run
```

> 🔐 On first launch, it auto-creates `charity.db` with predefined users:
> - Admin: `admin1 / admin123`
> - Donor: `donor1 / donor123`
> - Fundraiser: `fundraiser1 / fund123`

---

## 📈 Visuals

- Dashboard bar/pie charts (JavaFX)
- Exportable donation and campaign reports
- Real-time notification system
- Role-specific UI & sidebar navigation

---

## 📄 License

MIT License — you're free to use, remix, and learn from this.
