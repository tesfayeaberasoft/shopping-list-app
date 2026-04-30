# 🛒 Shopping List App

A feature-rich Android shopping list application with budget tracking, pantry management, and multi-language support.

## 🎨 App Icon

The Shopping List App features a custom-designed adaptive icon combining a shopping cart with a checklist design:

- **Shopping Cart**: Represents the core shopping functionality
- **Checklist Paper**: Shows task management and organization
- **Grocery Items**: Milk (blue), Apple (green), Bread (brown) with checkmarks
- **Color Scheme**: Orange (#FF8C00) primary, Green (#228B22) for checkmarks, Brown (#8B7355) for text

![App Icon - Shopping Cart with Checklist](app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

**Icon Features:**
- Vector-based adaptive design (scales to all screen densities)
- Android 8.0+ (API 26+) support with foreground/background layers
- Android 13+ (API 33+) monochrome support for themed icons
- Perfect rendering at any resolution

## 📱 Overview

Shopping List App is a comprehensive mobile application designed to help users manage their shopping efficiently. It combines shopping list management with budget tracking, pantry inventory, reminders, and detailed analytics to provide a complete shopping experience.

### Key Features

- **Shopping List Management**: Create, edit, and organize shopping lists with categories and priorities
- **Budget Tracking**: Set budgets and track spending across shopping lists
- **Pantry Inventory**: Manage pantry items with expiry date tracking and low stock alerts
- **Reminders & Notifications**: Set reminders for shopping days and low stock items
- **Barcode Scanning**: Quickly add items using barcode scanning
- **Voice Input**: Add items using voice commands
- **Shopping History**: Track purchase history and analyze spending patterns
- **Analytics Dashboard**: View detailed spending analytics and reports
- **List Sharing**: Share shopping lists with other users
- **Multi-Language Support**: English and Amharic language support
- **Dark/Light Theme**: Customizable app theme
- **Cloud Sync**: Synchronize data across devices with Firebase
- **Offline Support**: Full functionality with local database

## 🚀 Getting Started

### Prerequisites

- Android Studio 4.0 or higher
- Android SDK 21 (API Level 21) or higher
- Java 8 or higher
- Gradle 6.7 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/tesfayeaberasoft/shopping-list-app.git
   cd shopping-list-app
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Click "Open"

3. **Configure Firebase**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Download `google-services.json`
   - Place it in the `app/` directory

4. **Build and Run**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

## 📋 Project Structure

```
shopping-list-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/shoppinglist/
│   │   │   │   ├── activities/          # Activity classes
│   │   │   │   ├── fragments/           # Fragment classes
│   │   │   │   ├── adapters/            # RecyclerView adapters
│   │   │   │   ├── database/            # Room database
│   │   │   │   ├── models/              # Data models
│   │   │   │   ├── repository/          # Data repositories
│   │   │   │   ├── services/            # Background services
│   │   │   │   ├── utils/               # Utility classes
│   │   │   │   └── viewmodels/          # ViewModel classes
│   │   │   ├── res/
│   │   │   │   ├── layout/              # XML layouts
│   │   │   │   ├── drawable/            # Drawable resources
│   │   │   │   ├── values/              # String and color resources
│   │   │   │   ├── values-am/           # Amharic translations
│   │   │   │   ├── values-de/           # German translations
│   │   │   │   ├── anim/                # Animation resources
│   │   │   │   └── mipmap-*/            # App icons
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/                 # Instrumented tests
│   │   └── test/                        # Unit tests
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

## 🏗️ Architecture

The app follows the **MVVM (Model-View-ViewModel)** architecture pattern with the following components:

### Database Layer
- **Room Database**: Local SQLite database for offline support
- **DAOs**: Data Access Objects for database operations
- **Entities**: Database entity classes

### Repository Layer
- **LocalRepository**: Handles local database operations
- **CloudRepository**: Handles Firebase operations
- **SyncManager**: Manages data synchronization

### UI Layer
- **Activities**: Main screens of the application
- **Fragments**: Reusable UI components
- **Adapters**: RecyclerView adapters for list display

### ViewModel Layer
- **ViewModels**: Manage UI-related data and business logic
- **LiveData**: Observable data holders for reactive updates

## 🔧 Technologies Used

### Core Android
- Android SDK 21+
- AndroidX libraries
- Material Design 3

### Database
- Room Database
- SQLite

### Backend
- Firebase Authentication
- Firebase Firestore
- Firebase Storage
- Firebase Cloud Messaging

### Libraries
- **Lifecycle**: ViewModel, LiveData
- **Navigation**: Fragment navigation
- **Glide**: Image loading
- **Retrofit**: HTTP client
- **Gson**: JSON serialization
- **CircleImageView**: Circular image views
- **Material Components**: Material Design UI

### Testing
- JUnit 4
- Espresso
- Mockito

## 📱 Features in Detail

### 1. Shopping List Management
- Create multiple shopping lists
- Organize items by categories
- Set priority levels (High, Medium, Low)
- Add quantities and units
- Add notes to items
- Mark items as purchased
- Clear completed items

### 2. Budget Tracking
- Set budget for shopping lists
- Track spending in real-time
- View budget vs actual spending
- Get alerts when over budget
- Analyze spending patterns

### 3. Pantry Inventory
- Add pantry items
- Track current quantity
- Set minimum quantity alerts
- Track expiry dates
- Get low stock notifications
- Auto-suggest items for shopping list

### 4. Reminders & Notifications
- Set shopping day reminders
- Low stock alerts
- Expiry date reminders
- Customizable notification frequency
- Repeat reminders (Daily, Weekly, Monthly)

### 5. Barcode Scanning
- Scan product barcodes
- Auto-populate item details
- Quick item addition
- Requires camera permission

### 6. Voice Input
- Add items using voice commands
- Supports multiple languages
- Requires microphone permission

### 7. Shopping History
- Track all purchases
- View purchase history
- Analyze spending trends
- Export purchase data

### 8. Analytics Dashboard
- Total spending overview
- Category-wise spending
- Monthly/Weekly reports
- Most bought items
- Most expensive items
- Spending trends

### 9. List Sharing
- Generate share codes
- Share lists with other users
- Collaborative shopping
- Real-time synchronization

### 10. Multi-Language Support
- English (en)
- Amharic (am)
- German (de)
- Easy language switching

## 🌐 Language Support

### Supported Languages
- **English**: Full support
- **Amharic (አማርኛ)**: Complete translation
### Adding New Languages
1. Create `values-[language-code]/` directory
2. Add `strings.xml` with translations
3. Add `arrays.xml` for array resources
4. Update language selection in Settings

## 🎨 UI/UX Features

### Material Design 3
- Modern Material Design components
- Smooth animations and transitions
- Responsive layouts
- Adaptive icons

### Themes
- Light Mode
- Dark Mode
- System Default

### Accessibility
- Content descriptions
- Keyboard navigation
- Screen reader support
- High contrast support

## 🔐 Security Features

### Authentication
- Firebase Authentication
- Email/Password login
- Google Sign-In
- Session management

### Data Protection
- Encrypted local database
- Secure cloud storage
- HTTPS communication
- User data privacy

## 📊 Database Schema

### Main Entities
- **User**: User account information
- **ShoppingList**: Shopping list metadata
- **ShoppingItem**: Individual shopping items
- **PantryItem**: Pantry inventory items
- **Reminder**: Reminder configurations
- **ShoppingHistory**: Purchase history records

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Test Coverage
- Database operations
- ViewModel logic
- Repository functions
- Adapter functionality

## 📦 Build & Release

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Build Configuration
- Min SDK: 21 (Android 5.0)
- Target SDK: 34 (Android 14)
- Compile SDK: 34

## 🚀 Deployment

### Firebase Setup
1. Create Firebase project
2. Enable Authentication
3. Enable Firestore Database
4. Enable Cloud Storage
5. Enable Cloud Messaging
6. Download `google-services.json`

### App Distribution
- Google Play Store
- Firebase App Distribution
- Direct APK distribution

## 📝 API Documentation

### REST Endpoints
- User Management
- Shopping List Operations
- Item Management
- Analytics Data

### Firebase Collections
- `users/`: User profiles
- `shopping_lists/`: Shopping lists
- `shopping_items/`: Shopping items
- `pantry_items/`: Pantry inventory
- `reminders/`: Reminder settings

## 🐛 Known Issues

- Voice input may not work on all devices
- Barcode scanning requires good lighting
- Offline sync may have delays
- Some animations may lag on low-end devices

## 🔄 Version History

### Version 1.0.0 (Current)
- Initial release
- Core features implemented
- Multi-language support
- Firebase integration
- Analytics dashboard

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support, email: tesfayeaberalingane@gmail.com
## 🙏 Acknowledgments

- Material Design team for design guidelines
- Firebase team for backend services
- Android community for libraries and tools
- All contributors and testers

## 📚 Resources

### Documentation
- [Android Developer Guide](https://developer.android.com)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Material Design](https://material.io/design)
- [Room Database](https://developer.android.com/training/data-storage/room)

### Tutorials
- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [RecyclerView Guide](https://developer.android.com/guide/topics/ui/layout/recyclerview)

## 🎯 Future Enhancements

- [ ] Offline-first architecture
- [ ] Advanced analytics
- [ ] AI-powered recommendations
- [ ] Receipt scanning
- [ ] Price comparison
- [ ] Store locator
- [ ] Loyalty program integration
- [ ] Social features
- [ ] Meal planning integration
- [ ] Recipe suggestions

## 📱 Screenshots


### Main Screen
Main shopping lists overview with quick add functionality and empty state handling.

![Main Screen](docs/screenshots/main_screen.png)

- Shopping lists overview
- Quick add button
- Empty state
- List management options

### Shopping List Detail
Detailed view of individual shopping lists with items organized by categories.

![Shopping List Detail](docs/screenshots/list_detail.png)

- Item list with categories
- Budget progress indicator
- Completion status tracking
- Item quantity and pricing
- Mark items as purchased

### Add/Edit Item
Interface for adding and editing shopping items with detailed options.

![Add/Edit Item](docs/screenshots/add_item.png)

- Item name input
- Category selection
- Quantity and unit selection
- Price input
- Notes field
- Priority level selection

### Analytics Dashboard
Comprehensive spending analytics and reports.

![Analytics Dashboard](docs/screenshots/analytics.png)

- Total spending overview
- Category-wise spending breakdown
- Monthly/Weekly spending trends
- Most bought items list
- Most expensive items list
- Budget vs actual comparison

### Pantry Management
Inventory management with expiry date tracking.

![Pantry Management](docs/screenshots/pantry.png)

- Pantry items list
- Current quantity display
- Expiry date tracking
- Low stock alerts
- Add to shopping list option

### Reminders
Reminder configuration and management interface.

![Reminders](docs/screenshots/reminders.png)

- Reminder list view
- Create new reminder
- Set reminder frequency
- Notification settings
- Edit/Delete reminders

### Settings
Application settings and preferences.

![Settings](docs/screenshots/settings.png)

- Language selection (English, Amharic, German)
- Theme preferences (Light, Dark, System)
- Account management
- Notification settings
- About app information

### Barcode Scanner
Quick item addition using barcode scanning.

![Barcode Scanner](docs/screenshots/barcode_scanner.png)

- Camera preview
- Barcode detection
- Auto-populate item details
- Quick add to list

### Voice Input
Add items using voice commands.

![Voice Input](docs/screenshots/voice_input.png)

- Voice recording interface
- Speech recognition
- Item confirmation
- Quick add option

### Shopping History
Track and analyze purchase history.

![Shopping History](docs/screenshots/history.png)

- Purchase history list
- Date-wise filtering
- Spending summary
- Export options
- Detailed purchase information

### Share List
Collaborative shopping with list sharing.

![Share List](docs/screenshots/share_list.png)

- Generate share code
- Share with users
- Real-time synchronization
- Collaborative editing
- User permissions

## 🔗 Links

- **GitHub**: https://github.com/tesfayeaberasoft/shopping-list-app
- **Firebase Console**: https://console.firebase.google.com
- **Google Play Store**: [Coming Soon]

## 📋 Checklist for New Developers

- [ ] Clone repository
- [ ] Install Android Studio
- [ ] Configure Firebase
- [ ] Build and run app
- [ ] Explore codebase
- [ ] Read architecture documentation
- [ ] Set up development environment
- [ ] Run tests
- [ ] Create feature branch

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Contact

- **Email**: tesfayeaberalingane@gmail.com
- **GitHub**: [@tesfayeaberasoft](https://github.com/tesfayeaberasoft)


---

**Last Updated**: April 30, 2026

**Version**: 1.0.0

**Status**: Active Development

Made with ❤️ Tesfaye A.
