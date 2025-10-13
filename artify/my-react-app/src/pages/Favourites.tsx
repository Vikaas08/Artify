import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { useAuth } from "../AuthContext.tsx";
// import Star from "../components/bookmark.tsx";
import { Card, Row, Col, Alert } from "react-bootstrap";

const Favourites = () => {
  const [favorites, setFavorites] = useState<any[]>([]);
  const [spinner, setSpinner] = useState<boolean>(true);
  const [currentTime, setCurrentTime] = useState<number>(Date.now());
  const { toggleBookmark } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    axios
      .get("https://hw3-cs571-spring-25.uw.r.appspot.com/api/favourites", {
        withCredentials: true,
      })
      .then((res) => {
        console.log(res.data);
        setFavorites(res.data);

        setSpinner(false);
      })
      .catch((err) => {
        console.log("Error fetching favorites:", err);
        setSpinner(false);
      });

    const interval = setInterval(() => {
      setCurrentTime(Date.now());
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  if (spinner) {
    return <div>Add Spinner</div>;
  }

  function timeAgo(timestamp: string) {
    const now = currentTime;
    const then = new Date(timestamp).getTime();
    const diffInSeconds = Math.floor((now - then) / 1000);
    const diffInMinutes = Math.floor(diffInSeconds / 60);

    if (diffInSeconds < 60) {
      return `${diffInSeconds} second${diffInSeconds === 1 ? "" : "s"} ago`;
    } else if (diffInMinutes < 60) {
      return `${diffInMinutes} minute${diffInMinutes === 1 ? "" : "s"} ago`;
    } else {
      return `${Math.floor(diffInMinutes / 60)} hour${
        Math.floor(diffInMinutes / 60) === 1 ? "" : "s"
      } ago`;
    }
  }

  function handleRemove(id: string, img: string) {
    console.log("remove called");
    toggleBookmark(id, img);

    setFavorites((prevFavorites) =>
      prevFavorites.filter((favorite) => favorite.id !== id)
    );
  }

  function createCard(cardItem: any) {
    let favourite = {
      id: "",
      fullname: "",
      dob: "",
      dod: "",
      nationality: "",
      profileImageUrl: "",
      timestamp: "",
    };

    try {
      favourite.id = cardItem?.id;
      favourite.fullname = cardItem?.fullname ?? null;

      favourite.dob = cardItem?.dob ?? null;
      favourite.dod = cardItem?.dod ?? null;
      favourite.nationality = cardItem?.nationality ?? null;
      favourite.profileImageUrl =
        cardItem?.profileImageUrl ?? "/artsy_logo.svg";
      favourite.timestamp = cardItem?.timestamp ?? null;
    } catch (err) {
      console.log("Error while retrieving favourites information", err);
    }

    function handleFavCardClick() {
      const { id, profileImageUrl } = favourite;

      navigate(
        `/?q=${encodeURIComponent(id)}&img=${encodeURIComponent(
          profileImageUrl
        )}`
      );
    }

    return (
      <Col key={favourite.id}>
        <Card
          className="favCard"
          key={favourite.id}
          onClick={handleFavCardClick}
        >
          <Card.Img className="favs" src={favourite.profileImageUrl} />
          <Card.ImgOverlay>
            <Card.Text>
              <span className="fs-4 fw-bold">{favourite.fullname}</span>
              <br />
              {favourite.dob} - {favourite.dod}
              <br />
              {favourite.nationality}
            </Card.Text>
            <p className="realtime">{timeAgo(favourite.timestamp)}</p>
            <a
              href="#"
              onClick={(e) => {
                e.stopPropagation();
                handleRemove(favourite.id, favourite.profileImageUrl);
              }}
              className="remove"
            >
              {" "}
              Remove
            </a>
          </Card.ImgOverlay>
        </Card>
      </Col>
    );
  }

  const sortedFavorites = [...favorites].sort((a, b) => {
    return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime();
  });

  return (
    <div>
      <Header />
      <div className="container mt-5 mb-5">
        <div className="row justify-content-center">
          <div className="col-md-10 mb-5">
            {!spinner && sortedFavorites.length === 0 ? (
              <Alert className="mt-2" variant="danger">
                No favorite artists.
              </Alert>
            ) : (
              <Row xs={1} sm={2} md={3} className="g-4">
                {sortedFavorites.map(createCard)}
              </Row>
            )}
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default Favourites;
