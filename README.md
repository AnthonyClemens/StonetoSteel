<!-- PROJECT SHIELDS -->
[![Commits][commits-shield]][commits-url]
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/AnthonyClemens/StoneToSteel">
    <img src="I WILL PUT THE LOGO HERE WHEN I FIGURE IT OUT" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">StoneToSteel</h3>

  <p align="center">
    A real-time strategy and tower defense game where you start as a single character, gather resources, fight enemies, and build a civilization while progressing through a tech tree across different ages.
    <br />
    <a href="https://github.com/AnthonyClemens/StoneToSteel"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/AnthonyClemens/StoneToSteel">View Code</a>
    ·
    <a href="https://github.com/AnthonyClemens/StoneToSteel/issues/new?labels=bug">Report Bug</a>
    ·
    <a href="https://github.com/AnthonyClemens/StoneToSteel/issues/new?labels=enhancement">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#documentation">Documentation</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

StoneToSteel is a real-time strategy and tower defense game that combines survival, exploration, and strategic planning. Players begin as a single character in a vast, procedurally generated world. The goal is to gather resources, fight off enemies that spawn at night, and build a thriving civilization.

Key features of the game include:
- **Resource Gathering**: Collect wood, stone, and other materials to construct buildings or craft tools and weapons.
- **Enemy Waves**: Survive nightly enemy attacks that grow stronger as you progress.
- **Biomes**: Explore diverse biomes, each with unique resources, challenges, and enemies.
- **Tech Tree**: Advance through different ages, unlocking new technologies, buildings, and units.
- **Building and Expansion**: Construct defensive structures, houses, and other facilities to grow your civilization.
- **Day-Night Cycle**: Manage your time wisely as enemies only spawn at night, while the day is ideal for exploration and resource gathering.
- **Dynamic Gameplay**: Adapt to changing challenges as you progress through the game.

StoneToSteel offers a unique blend of RTS and tower defense mechanics, providing players with the freedom to strategize and build their civilization while defending against increasingly difficult threats.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running, follow these steps.

### Prerequisites

You will need Java 17 or higher and Maven installed.

* Java 17
  ```sh
  sudo apt install openjdk-17-jdk
  ```
  [Download Java for other platforms](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)

* Maven
  ```sh
  sudo apt install maven
  ```
  [Download Maven for other platforms](https://maven.apache.org/download.cgi)

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/AnthonyClemens/StoneToSteel.git
   ```
2. Navigate to the project directory
   ```sh
   cd StoneToSteel
   ```
3. Build the project using Maven
   ```sh
   mvn clean install
   ```
4. Run the game
   ```sh
   java -jar target/stonetosteel-0.2-jar-with-dependencies.jar
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- DOCUMENTATION -->
## Documentation

This section provides an overview of the key classes in the project and their responsibilities.

### Core Classes
- **Main**: The entry point of the game. Initializes the game states and manages settings like resolution and frame rate.
- **Game**: Represents the main gameplay state, including player interactions, rendering, and game logic.
- **Settings**: Manages game settings such as resolution, sound levels, and other configurations.

### Gameplay Mechanics
- **Player**: Represents the player character, including movement, animations, and interactions with the game world.
- **DayNightCycle**: Handles the day-night cycle, including time progression and visual overlays for different times of the day.
- **Calender**: Tracks the in-game date and manages month and year transitions.

### World Generation
- **ChunkManager**: Manages the procedural generation and rendering of world chunks.
- **PerlinNoise**: Provides noise generation for terrain and biome creation.
- **GameObjectGenerator**: Generates game objects like trees, fish, and other environmental elements.

### Rendering
- **IsoRenderer**: Handles isometric rendering of the game world.
- **SpriteManager**: Manages sprite sheets and their associated textures to allow for efficient image retrieval.

### GUI Components
- **GUIElement**: Base class for all GUI elements.
- **Slider**: A GUI element for adjusting values like sound or brightness.
- **Carousel**: A GUI element for selecting options from a list.
- **ColorTextButton**: A customizable button with text and background color.

### States
- **MainMenu**: Represents the main menu of the game.
- **NewGame**: Handles the creation of a new game, including world name and seed input.
- **SettingsMenu**: Provides access to video, sound, and control settings.
- **VideoSettings**: Manages video-related settings like resolution and fullscreen mode.
- **SoundSettings**: Manages sound-related settings like volume levels.
- **ControlSettings**: Allows customization of game controls.

### Sound Management
- **JukeBox**: Manages background music playback.
- **SoundBox**: Handles sound effects for different game events.
- **AmbientSoundManager**: Plays ambient sounds based on the player's location and time of day.

### Utilities
- **SaveLoadManager**: Handles saving and loading game states.
- **CollisionHandler**: Manages collision detection between game objects.
- **DisplayHUD**: Renders the heads-up display (HUD) with information like the current date and time.
- **DebugGUI**: Provides debugging tools for developers and testers.

### Game Objects
- **GameObject**: Base class for all game objects.
- **SingleTileObject**: Represents objects that are a single sprite.
- **MultiTileObject**: Represents objects that use multiple sprites.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Top contributors:

<a href="https://github.com/AnthonyClemens/StoneToSteel/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=AnthonyClemens/StoneToSteel" alt="contrib.rocks image" />
</a>

<!-- CONTACT -->
## Contact

Anthony Clemens - [anthony.clemens831@gmail.com](mailto:anthony.clemens831@gmail.com)

Project Link: [https://github.com/AnthonyClemens/StoneToSteel](https://github.com/AnthonyClemens/StoneToSteel)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* [Slick2D](http://slick.ninjacave.com/)
* [LWJGL](https://www.lwjgl.org/)
* [My wife, Marissa, for always supporting me](https://github.com/mclemens817)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/AnthonyClemens/StoneToSteel.svg?style=for-the-badge
[contributors-url]: https://github.com/AnthonyClemens/StoneToSteel/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/AnthonyClemens/StoneToSteel.svg?style=for-the-badge
[forks-url]: https://github.com/AnthonyClemens/StoneToSteel/network/members
[stars-shield]: https://img.shields.io/github/stars/AnthonyClemens/StoneToSteel.svg?style=for-the-badge
[stars-url]: https://github.com/AnthonyClemens/StoneToSteel/stargazers
[issues-shield]: https://img.shields.io/github/issues/AnthonyClemens/StoneToSteel.svg?style=for-the-badge
[issues-url]: https://github.com/AnthonyClemens/StoneToSteel/issues
[commits-shield]: https://img.shields.io/github/commit-activity/t/AnthonyClemens/StoneToSteel?style=for-the-badge
[commits-url]: https://github.com/AnthonyClemens/StoneToSteel/commits
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/anthony-clemens831
